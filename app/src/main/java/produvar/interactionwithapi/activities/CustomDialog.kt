package produvar.interactionwithapi.activities

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.view.Window
import kotlinx.android.synthetic.main.custom_error_alert.*
import produvar.interactionwithapi.R

class CustomDialog(val activity: Activity, val message: String, val onClose: () -> Unit) : AppCompatDialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_error_alert)
        error_message.text = message
        proceed_button.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        onClose()
        super.onBackPressed()
    }

    override fun onStop() {
        onClose()
        super.onStop()
    }



}
