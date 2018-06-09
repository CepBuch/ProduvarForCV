package produvar.interactionwithapi.activities.tagInfo

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Display
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
import org.jetbrains.anko.toast
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.TagChecker
import produvar.interactionwithapi.helpers.TestData
import produvar.interactionwithapi.helpers.changeStatusBarColor
import produvar.interactionwithapi.model.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TagInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_info)
        button_back.setOnClickListener { onBackPressed() }

        changeStatusBarColor(R.color.statusbarMain, true)

        // First, try to process a tag as it came from NFC reader
        // Then, ty to get tag from previous activity (if the tag was scanned by camera, tag content
        // was sent to this activity via extras)
        val extras = intent.extras
        if (!tryProcessNfcTag() && extras != null) {
            val barcode = extras.getString("barcode")
            processTag(barcode)
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
                            // (as plain/text formats starts with language  description code and the content)
                            processTag(rawContent.drop(2))
                        }
                    }
                }
            }
            if (!flag) showScanError()
            true
        } else false
    }


    private fun processTag(tagContent: String) {
        // http://... or https://.. or 12425...
        if (TagChecker.isOrderTagValid(tagContent)) {
            // Getting basic info about an order (for both authorized and anonymous users)
            val basicOrderInfo = getBasicOrderInfo(tagContent)
            if (basicOrderInfo == null) {
                showScanError()
                return
            }

            if (basicOrderInfo.manufacturer != null) {
                showManufacturerInfo(basicOrderInfo.manufacturer)
            }

            // If we got order code in return and got authorization token (TODO)
            // Trying to get more authorized information
            if (!basicOrderInfo.ordercode.isNullOrBlank()) {
                val orderInfo = getOrderInfo(basicOrderInfo.ordercode!!) ?: return
                showOrderInfo(orderInfo)
            }
        } else showScanError()
    }

    private fun showManufacturerInfo(manufacturer: BasicManufacturerView) {
        card_manufacturer.visibility = if (manufacturer.containsUsefulData()) {
            View.VISIBLE
        } else {
            showScanError()
            View.GONE
        }

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


        showOrderItems(order.filterOrderItems())
        showStatusFlowItems(order.filterStatusFlow())
        showProcessItems(order.filterOrderProcesses())
    }


    private fun showOrderItems(items: List<OrderItem>) {
        card_items.visibility = if (items.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE
        for (item in items) {
            displayOrderItem(item.label!!)
        }
    }

    private fun displayOrderItem(itemName: String) {
        val textView = layoutInflater.inflate(R.layout.item_order,
                order_items_container, false) as TextView
        textView.text = itemName
        order_items_container.addView(textView)
    }


    private fun showStatusFlowItems(items: List<WorkFlowStep>) {
        card_status_flow.visibility = if (items.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE

        val listSize = items.size
        for ((index, value) in items.withIndex()) {
            displayStatusFlowItem(value.status!!, index == listSize - 2, index == listSize - 1)
        }

        val currentStep = items.last()
        val futureSteps = TestData.futureSteps
        showStatusUpdateCard(currentStep, futureSteps)
    }

    private fun displayStatusFlowItem(processName: String, isPenultimate: Boolean, isLast: Boolean) {
        val layout = layoutInflater.inflate(R.layout.item_status_flow,
                statusflow_items_container, false) as LinearLayout
        val textView = layout.findViewById<TextView>(R.id.textview_process_content)
        val connectingLine = layout.findViewById<View>(R.id.connecting_line)

        textView.text = processName
        if (isPenultimate) {
            connectingLine.setBackgroundColor(ContextCompat.getColor(this@TagInfoActivity, R.color.produvarOrange))
        } else if (isLast) {
            with(textView) {
                setBackgroundResource(R.drawable.process_item_shape_current)
                setTextColor(ContextCompat.getColor(this@TagInfoActivity, R.color.produvarOrange))
                typeface = Typeface.DEFAULT_BOLD
            }
            connectingLine.visibility = View.GONE
        }
        statusflow_items_container.addView(layout)
    }

    private fun showStatusUpdateCard(currentStep: WorkFlowStep, futureSteps: List<WorkFlowStep>) {
        card_update_status.visibility = if (futureSteps.isNotEmpty()) {
            View.VISIBLE
        } else View.GONE

        val spinnerArray = futureSteps.map { it.status!! }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            toast("Current step: ${currentStep.status}\n" +
                    "New step: ${update_spinner_future_steps.selectedItem.toString()}\n" +
                    "Location: ${update_location.text}")
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

    private fun showProcessItems(items: List<OrderProcess>) {
        card_process.visibility = if (items.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE

        val listSize = items.size
        for ((index, value) in items.withIndex()) {
            displayProcessItem(value.label!!, index == listSize - 1)
        }
    }

    private fun displayProcessItem(itemProcess: String, isLast: Boolean) {
        val splitted = itemProcess.split(" ")

        // checking whether process is in "yyyy-MM-dd HH:mm MESSAGE" format or not
        // if it is, displaying date in separate italic textview
        var time: String? = null
        lateinit var message: String
        if (splitted.size > 2) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            val potentiallyStrDate = splitted.take(2).joinToString(" ")
            time = try {
                sdf.parse(potentiallyStrDate)
                message = splitted.drop(2).joinToString(" ")
                potentiallyStrDate
            } catch (e: Exception) {
                message = itemProcess
                null
            }
        } else {
            message = itemProcess
        }

        val layout = layoutInflater.inflate(R.layout.item_process,
                process_items_container, false) as LinearLayout

        val timeTextView = layout.findViewById<TextView>(R.id.process_when)
        val messageTextView = layout.findViewById<TextView>(R.id.process_message)
        val line = layout.findViewById<View>(R.id.process_horizontal_line)


        timeTextView.visibility = if (time != null) {
            timeTextView.text = time
            View.VISIBLE
        } else View.GONE

        messageTextView.text = message

        if (isLast) {
            line.visibility = View.GONE
        }

        process_items_container.addView(layout)
    }


    private fun getBasicOrderInfo(tagContent: String): BasicOrderView? {
        return TestData.basicOrderView
    }

    private fun getOrderInfo(orderCode: String): Order? {
        return TestData.order
    }


    private fun showScanError() {
        content_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE
    }
}
