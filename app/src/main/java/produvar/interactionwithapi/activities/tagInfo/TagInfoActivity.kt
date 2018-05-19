package produvar.interactionwithapi.activities.tagInfo

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tag_info.*
import kotlinx.android.synthetic.main.card_items_view.*
import kotlinx.android.synthetic.main.toolbar_taginfo.*
import produvar.interactionwithapi.helpers.DpToPx
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.SpToPx
import produvar.interactionwithapi.helpers.changeStatusBarColor

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

    fun displayItem(itemName: String) {
        val textView = TextView(this)

        with(textView) {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setTextColor(ContextCompat.getColor(this@TagInfoActivity, R.color.produvarDark))
            setPadding(0, 5.DpToPx(this@TagInfoActivity), 0, 5.DpToPx(this@TagInfoActivity))
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_taginfo_items, 0, 0, 0)
            compoundDrawablePadding = 10.DpToPx(this@TagInfoActivity)
            // TODO: sp to px
            textSize = 16f
            text = itemName
        }

        items_container.addView(textView)
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
            displayItem(tagContent)
            displayItem(tagContent)
            displayItem(tagContent)
            displayItem(tagContent)
            displayItem("Left part of worktop X34")
            displayItem("Left part of worktop X34")
            displayItem("Left part of worktop X34")
            displayItem("Left part of worktop X34")
        } else showScanError()
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
