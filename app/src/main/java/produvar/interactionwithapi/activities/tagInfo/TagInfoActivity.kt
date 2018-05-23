package produvar.interactionwithapi.activities.tagInfo

import android.content.Intent
import android.graphics.Typeface
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import junit.framework.Test
import kotlinx.android.synthetic.main.activity_tag_info.*
import kotlinx.android.synthetic.main.card_items_view.*
import kotlinx.android.synthetic.main.card_manufacturer_view.*
import kotlinx.android.synthetic.main.card_order_view.*
import kotlinx.android.synthetic.main.card_process_view.*
import kotlinx.android.synthetic.main.card_statusflow_view.*
import kotlinx.android.synthetic.main.toolbar_taginfo.*
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.TagChecker
import produvar.interactionwithapi.helpers.TestData
import produvar.interactionwithapi.helpers.changeStatusBarColor
import produvar.interactionwithapi.model.*
import java.text.SimpleDateFormat
import java.util.*

class TagInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_info)
        button_back.setOnClickListener { onBackPressed() }

        changeStatusBarColor(R.color.statusbarMain, true)

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
        return if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            var flag = false
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null) {
                val messages = rawMessages.map { it as NdefMessage }
                if (messages.isNotEmpty()) {
                    val records = messages[0].records
                    if (records != null && records.isNotEmpty()) {
                        val payload = records[0].payload
                        val rawContent = String(payload.drop(1).toByteArray())
                        if (rawContent.startsWith("en")) {
                            flag = true
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
        if (TagChecker.isOrderTagValid(tagContent)) {
            val basicOrderInfo = getBasicOrderInfo(tagContent) ?: return
            showManufacturerInfo(basicOrderInfo.manufacturer)

            if (!basicOrderInfo.orderCode.isNullOrBlank()) {
                val orderInfo = getOrderInfo(basicOrderInfo.orderCode!!) ?: return
                showOrderInfo(orderInfo)
            }

        } else showScanError()
    }

    private fun showManufacturerInfo(manufacturer: BasicManufacturerView) {
        var atLeastOne = false

        manufacturer_name.visibility = if (!manufacturer.name.isNullOrBlank()) {
            manufacturer_name.text = manufacturer.name
            atLeastOne = true
            View.VISIBLE
        } else View.GONE

        manufacturer_website.visibility = if (!manufacturer.website.isNullOrBlank()) {
            manufacturer_website.text = manufacturer.website
            atLeastOne = true
            View.VISIBLE
        } else View.GONE

        manufacturer_email.visibility = if (!manufacturer.email.isNullOrBlank()) {
            manufacturer_email.text = manufacturer.email
            atLeastOne = true
            View.VISIBLE
        } else View.GONE

        manufacturer_phone.visibility = if (!manufacturer.phoneNumber.isNullOrBlank()) {
            manufacturer_phone.text = manufacturer.phoneNumber
            atLeastOne = true
            View.VISIBLE
        } else View.GONE

        card_manufacturer.visibility = if (atLeastOne) View.VISIBLE else View.GONE
    }


    private fun showOrderInfo(order: Order) {

        card_order.visibility = if (!order.dueDate.isNullOrBlank()) {
            order_dueDate.text = order.dueDate
            order_dueDate.visibility = View.VISIBLE
            View.VISIBLE
        } else View.GONE

        showOrderItems(order.items)
        showStatusFlowItems(order.statusFlow)
        showProcessItems(order.process)

        button_update_status.visibility = View.VISIBLE
    }

    private fun getBasicOrderInfo(tagContent: String): BasicOrderView? {
        return TestData.basicOrderView
    }

    private fun getOrderInfo(orderCode: String): Order? {
        return TestData.order
    }

    private fun showProcessItems(items: List<OrderProcess>) {
        val listSize = items.size
        val notEmptyItems = items.filterNot { it.label.isNullOrBlank() }

        card_process.visibility = if (notEmptyItems.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE

        for ((index, value) in notEmptyItems.withIndex()) {
            displayProcess(value.label!!, index == listSize - 1)
        }
    }

    private fun displayProcess(itemProcess: String, isLast: Boolean) {
        val splitted = itemProcess.split(' ')
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

    private fun showStatusFlowItems(items: List<WorkFlowStep>) {
        val listSize = items.size
        val notEmptyItems = items.filterNot { it.status.isNullOrBlank() }

        card_status_flow.visibility = if (notEmptyItems.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE

        for ((index, value) in notEmptyItems.withIndex()) {
            displayStatusFlowItem(value.status!!, index == listSize - 2, index == listSize - 1)
        }

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

    private fun showOrderItems(items: List<OrderItem>) {
        val notEmptyItems = items.filterNot { it.label.isNullOrBlank() }
        card_items.visibility = if (notEmptyItems.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE
        for (i in notEmptyItems) {
            displayOrderItem(i.label!!)
        }
    }


    private fun displayOrderItem(itemName: String) {
        val textView = layoutInflater.inflate(R.layout.item_order,
                order_items_container, false) as TextView
        textView.text = itemName
        order_items_container.addView(textView)
    }


    private fun showScanError() {
        content_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE
    }
}
