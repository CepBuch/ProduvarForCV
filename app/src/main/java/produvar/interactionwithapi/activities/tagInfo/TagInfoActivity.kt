package produvar.interactionwithapi.activities.tagInfo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tag_info.*
import kotlinx.android.synthetic.main.card_items_view.*
import kotlinx.android.synthetic.main.card_manufacturer_view.*
import kotlinx.android.synthetic.main.card_order_view.*
import kotlinx.android.synthetic.main.card_process_view.*
import kotlinx.android.synthetic.main.card_statusflow_view.*
import kotlinx.android.synthetic.main.card_update_status_view.*
import kotlinx.android.synthetic.main.toolbar_taginfo.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.toast
import produvar.interactionwithapi.Factory
import produvar.interactionwithapi.R
import produvar.interactionwithapi.dialogs.CustomOkDialog
import produvar.interactionwithapi.dialogs.UpdateYesNoDialog
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.enums.LoginType
import produvar.interactionwithapi.helpers.*
import produvar.interactionwithapi.models.*
import java.text.SimpleDateFormat
import java.util.*

class TagInfoActivity : AppCompatActivity() {

    private var currentCode: String? = null
    private var customDialog: CustomOkDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_info)
        button_back.setOnClickListener { onBackPressed() }
        button_refresh.setOnClickListener {
            refresh()
        }

        changeStatusBarColor(R.color.statusbarMain, true)

        // First, try to process a tag as it may came from NFC reader
        // Then, try to get tag from previous activity (if the tag was scanned by camera, tag content
        // was sent to this activity via extras)
        val extras = intent.extras
        if (!tryProcessNfcTag() && extras != null) {
            val barcode = extras.getString("barcode")
            processTag(barcode)
        }
    }

    private fun refresh(force: Boolean = false) {
        if (!force) {
            if (isConnected()) {
                if (currentCode != null) {
                    this.recreate()
                } else return
            } else {
                toast(getString(R.string.error_refresh))
            }
        } else {
            this.recreate()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        tryProcessNfcTag()
    }

    private fun tryProcessNfcTag(): Boolean {
        // Only ACTION_NDEF_DISCVERED tags
        return if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            var flag = false
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null) {
                val messages = rawMessages.map { it as NdefMessage }
                if (messages.isNotEmpty()) {
                    // tag should contain 1 message with 1 record in it, that contains url/numeric string
                    // If url, it should start with http:// or https://
                    val records = messages[0].records
                    if (records != null && records.isNotEmpty()) {
                        val payload = records[0].payload
                        val rawContent = String(payload.drop(1).toByteArray())
                        // Tag should be written as plain/text with UTF-8/UTF-16 and en language
                        if (rawContent.startsWith("en")) {
                            flag = true
                            // dropping language
                            // (as plain/text formats starts with language  description code and then the content)
                            processTag(rawContent.drop(2))
                        }
                    }
                }
            }
            if (!flag) {
                showScanError(getString(R.string.taginfo_error_message))
                button_refresh.visibility = View.GONE
            }
            true
        } else false
    }

    private fun processTag(tagContent: String) {
        currentCode = tagContent
        if (isConnected()) {
            // Getting basic info about an order (for both authorized and anonymous users)
            launch(UI) {
                showProgressBar(true)
                val provider = Factory.getApiProvider()
                val (basicOrderView, error) = provider.searchByScan(tagContent).await()
                when {
                    basicOrderView != null -> {
                        val (code, manufacturer) = basicOrderView.convertToModel()
                        if (manufacturer != null) {
                            showManufacturerInfo(manufacturer)
                            tryShowOrderInfo(code)
                        } else showScanError(getString(R.string.taginfo_error_message))
                    }
                    error != null -> {
                        showScanError(when (error) {
                            ErrorType.NOT_FOUND -> getString(R.string.taginfo_not_found)
                            else -> getString(R.string.taginfo_error_message)
                        })

                    }
                }
            }
        } else showScanError(getString(R.string.error_internet_connection))
    }

    private suspend fun tryShowOrderInfo(orderCode: String?) {
        val user = tryGetCurrentUser()
        // Trying to get more authorized information
        if (!orderCode.isNullOrBlank() && user != null) {
            val provider = Factory.getApiProvider()
            val (orderDTO, _) = provider.orderInfo(user, orderCode!!).await()
            val order = orderDTO?.convertToModel()
            if (order != null) {
                showOrderInfo(order)
            }
        } else if (user == null) {
            additional_info_tip.visibility = View.VISIBLE
        }
        showProgressBar(false)
    }


    private fun showManufacturerInfo(manufacturer: Manufacturer) {
        card_manufacturer.visibility = if (manufacturer.containsUsefulData()) {

            manufacturer_name.visibility = if (!manufacturer.name.isNullOrBlank()) {
                manufacturer_name.text = manufacturer.name
                View.VISIBLE
            } else View.GONE

            manufacturer_website.visibility = if (!manufacturer.website.isNullOrBlank()) {
                manufacturer_website.text = manufacturer.website
                View.VISIBLE
            } else View.GONE

            manufacturer_email.visibility = if (!manufacturer.email.isNullOrBlank()) {
                manufacturer_email.text = manufacturer.email
                View.VISIBLE
            } else View.GONE

            manufacturer_phone.visibility = if (!manufacturer.phoneNumber.isNullOrBlank()) {
                manufacturer_phone.text = manufacturer.phoneNumber
                View.VISIBLE
            } else View.GONE

            View.VISIBLE
        } else {
            showScanError(getString(R.string.taginfo_error_message))
            View.GONE
        }
    }

    private fun showOrderInfo(order: Order) {
        card_order.visibility = if (order.containsUsefulData()) {
            order_dueDate.visibility = if (!order.dueDate.isNullOrBlank()) {
                val dueDate = order.tryGetParsedDate()
                order_dueDate.text = if (dueDate != null) {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
                    format.format(dueDate)
                } else {
                    order.dueDate
                }
                View.VISIBLE
            } else View.GONE

            order_label.visibility = if (!order.label.isNullOrBlank()) {
                order_label.text = order.label
                View.VISIBLE
            } else View.GONE
            View.VISIBLE
        } else View.GONE

        showOrderItems(order.items)
        showStatusFlowItems(order.statusFlow)
        showProcessItems(order.process)
    }

    private fun showOrderItems(items: List<Item>) {
        card_items.visibility = if (items.isNotEmpty()) {
            items.forEach { displayOrderItem(it) }
            View.VISIBLE
        } else View.GONE
    }

    private fun displayOrderItem(item: Item) {
        val textView = layoutInflater.inflate(R.layout.item_order,
                order_items_container, false) as TextView
        textView.text = item.label
        order_items_container.addView(textView)
    }

    private fun showStatusFlowItems(items: List<WorkflowStep>) {
        card_status_flow.visibility = if (items.isNotEmpty()) {
            // Showing the first element (it shouldn't have connection line above)
            displayStatusFlowItem(items[0], true)
            // Showing other elements
            items.drop(1).forEach { displayStatusFlowItem(it) }

            // If we can extract current and at least one future steps, showing status update card
            val currentStep = items.find { it.isCurrent }
            val futureSteps = items.filter { it.isCurrent == false && it.isFinished == false }
            showStatusUpdateCard(currentStep, futureSteps)
            View.VISIBLE
        } else View.GONE
    }

    private fun displayStatusFlowItem(item: WorkflowStep, isFirst: Boolean = false) {
        val layout = layoutInflater.inflate(R.layout.item_status_flow,
                statusflow_items_container, false) as LinearLayout
        val textView = layout.findViewById<TextView>(R.id.textview_process_content)
        val connectingLine = layout.findViewById<View>(R.id.connecting_line)

        textView.text = item.status

        connectingLine.visibility = if (!isFirst) {
            connectingLine.setBackgroundResource(when {
                item.isFinished -> R.drawable.process_line_previous
                item.isCurrent -> R.drawable.process_line_current
                else -> R.drawable.process_line_future
            })
            View.VISIBLE
        } else View.GONE

        textView.setBackgroundResource(when {
            item.isFinished -> R.drawable.process_item_shape_previous
            item.isCurrent -> {
                textView.setTextColor(ContextCompat.getColor(this@TagInfoActivity, R.color.produvarOrange))
                textView.typeface = Typeface.DEFAULT_BOLD
                R.drawable.process_item_shape_current
            }
            else -> {
                textView.setTextColor(ContextCompat.getColor(this@TagInfoActivity, R.color.produvarOrangeTransparent))
                R.drawable.process_item_shape_future
            }
        })
        statusflow_items_container.addView(layout)
    }

    private fun showStatusUpdateCard(currentStep: WorkflowStep?, futureSteps: List<WorkflowStep>) {
        val currentUser = tryGetCurrentUser()
        card_update_status.visibility = if (futureSteps.isNotEmpty() && currentStep != null &&
                currentUser != null && currentUser.loginType == LoginType.PersonalAccount) {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    futureSteps.map { it.status })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            update_spinner_future_steps.adapter = adapter

            update_camera_button.setOnClickListener {
                // Open scan barcode activity if tag hasn't been scanned yet
                if (update_location.text.isBlank()) {
                    val intent = Intent(this, ScanLocationActivity::class.java)
                    startActivityForResult(intent, Constants.SCAN_LOCATION)
                } else {
                    // Clearing the field first if a update_location already has scanned tag
                    update_location.setText("")
                    update_camera_button.setImageResource(R.drawable.ic_taginfo_camera_black)
                }
            }

            button_update_status.setOnClickListener {
                updateStatus(currentStep.status, update_spinner_future_steps.selectedItem.toString())
            }

            View.VISIBLE
        } else View.GONE
    }

    private fun updateStatus(currentStatus: String, newStatus: String) {
        val user = tryGetCurrentUser()
        val code = currentCode ?: return
        val description = update_description.text.toString()
        val location = update_location.text.toString()
        if (user != null) {
            val dialog = UpdateYesNoDialog(this, currentStatus, newStatus)
            {
                if (isConnected()) {
                    launch(UI) {
                        button_update_status.visibility = View.INVISIBLE
                        update_progress.visibility = View.VISIBLE
                        val provider = Factory.getApiProvider()
                        val res = provider.orderStatusUpdate(user, code, currentStatus, newStatus, description, location).await()
                        button_update_status.visibility = View.VISIBLE
                        update_progress.visibility = View.INVISIBLE
                        showPopUpInfo(res)
                    }

                } else showPopUpInfo(ErrorType.NOT_CONNECTED)
            }

            with(dialog) {
                show()
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.SCAN_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                val code = data?.getStringExtra(Constants.LOCATION_RESULT) ?: return
                update_location.setText(code)
                // Changing button icon to "Clear"
                update_camera_button.setImageResource(R.drawable.ic_taginfo_location_clear)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showProcessItems(items: List<ProcessStep>) {
        card_process.visibility = if (items.isNotEmpty()) {
            items.dropLast(1).forEach { displayProcessItem(it) }
            displayProcessItem(items.last(), true)
            View.VISIBLE
        } else View.GONE
    }

    private fun displayProcessItem(item: ProcessStep, isLast: Boolean = false) {

        val layout = layoutInflater.inflate(R.layout.item_process,
                process_items_container, false) as LinearLayout

        val timeTextView = layout.findViewById<TextView>(R.id.process_when)
        val messageTextView = layout.findViewById<TextView>(R.id.process_message)
        val line = layout.findViewById<View>(R.id.process_horizontal_line)

        timeTextView.visibility = if (!item.time.isNullOrBlank()) {
            timeTextView.text = item.time
            View.VISIBLE
        } else View.GONE

        messageTextView.text = if (!item.time.isNullOrBlank() && !item.description.isNullOrBlank()) {
            item.description
        } else {
            item.label
        }
        line.visibility = if (isLast) View.GONE else View.VISIBLE

        process_items_container.addView(layout)
    }

    private fun showScanError(errorMessage: String) {
        showProgressBar(false)
        error_message.text = errorMessage
        content_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE
    }

    private fun showPopUpInfo(errorType: ErrorType?) {
        val message = when (errorType) {
            null -> getString(R.string.taginfo_update_success)
            ErrorType.NOT_FOUND -> getString(R.string.taginfo_error_status)
            ErrorType.NOT_CONNECTED -> getString(R.string.error_internet_connection)
            else -> getString(R.string.taginfo_error_unknown)
        }
        customDialog = CustomOkDialog(this, message) {
            customDialog = null
            if (errorType == null) {
                refresh(true)
            }
        }
        customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog?.show()
    }

    private fun showProgressBar(show: Boolean) {
        content_view.visibility = if (show) View.GONE else View.VISIBLE
        progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onPause() {
        customDialog?.hide()
        super.onPause()
    }
}
