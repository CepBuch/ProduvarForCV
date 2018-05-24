package produvar.interactionwithapi.activities.help

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_help.*
import produvar.interactionwithapi.R
import android.content.Intent
import android.net.Uri


class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        button_close.setOnClickListener { onBackPressed() }
        button_contact.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.help_produvar_url)))
            startActivity(browserIntent)
        }

    }

}
