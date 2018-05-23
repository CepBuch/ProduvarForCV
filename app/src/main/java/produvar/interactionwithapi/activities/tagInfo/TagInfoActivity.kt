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
import kotlinx.android.synthetic.main.activity_tag_info.*
import kotlinx.android.synthetic.main.card_items_view.*
import kotlinx.android.synthetic.main.card_process_view.*
import kotlinx.android.synthetic.main.card_statusflow_view.*
import kotlinx.android.synthetic.main.toolbar_taginfo.*
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.changeStatusBarColor
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
        if (isTagContentValid(tagContent)) {
            val testListOrderItems = mutableListOf<String>(tagContent)
            repeat(2) { testListOrderItems.add("Worktop X34 for Johnson family") }
            testListOrderItems.addAll(listOf<String>("everything is added dynamically",
                    "according to server response", "if server doesn't provide data for one or other card it won't be shown",
                    "btw, not sure if icons in this card are ok"))
            val testListStatusFlow = listOf<String>("1st step", "2nd step", "these", "are", "steps", "of execution",
                    "transportation", tagContent)
            val testListOfProcesses = listOf<String>("2017-03-03 15:33 Pete Manson polished the blade",
                    "2018-05-23 2:37 if it has date it will be highlighted", "otherwise, just shown as a string",
                    "i'm also thinking of making every card expandable/collapsable",
                    "2018-05-23 2:46 When opened, probably show ''expanded'' only Manufacturer and Order info and collapse others",
                    tagContent)


            showOrderItems(testListOrderItems)
            showStatusFlowItems(testListStatusFlow)
            showProcessItems(testListOfProcesses)

        } else showScanError()
    }

    private fun showProcessItems(items: List<String>) {
        card_process.visibility = if (items.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE

        val listSize = items.size

        for ((index, value) in items.withIndex()) {
            displayProcess(value, index == listSize - 1)
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

        if(isLast){
            line.visibility = View.GONE
        }


        process_items_container.addView(layout)

    }

    private fun showStatusFlowItems(items: List<String>) {

        card_status_flow.visibility = if (items.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE

        val listSize = items.size
        for ((index, value) in items.withIndex()) {
            displayStatusFlowItem(value, index == listSize - 2, index == listSize - 1)
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

    private fun showOrderItems(items: List<String>) {
        card_items.visibility = if (items.isEmpty()) {
            View.GONE
            return
        } else View.VISIBLE
        for (i in items) {
            displayOrderItem(i)
        }
    }


    private fun displayOrderItem(itemName: String) {
        val textView = layoutInflater.inflate(R.layout.item_order,
                order_items_container, false) as TextView
        textView.text = itemName
        order_items_container.addView(textView)
    }


    private fun isTagContentValid(tagContent: String): Boolean {
        return tagContent.startsWith("https://") || tagContent.startsWith("http://") ||
                tagContent.all { it.isDigit() }
    }

    private fun showScanError() {
        content_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE
    }
}
