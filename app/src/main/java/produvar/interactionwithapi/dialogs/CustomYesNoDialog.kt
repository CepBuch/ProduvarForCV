package produvar.interactionwithapi.dialogs

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.Window
import kotlinx.android.synthetic.main.dialog_ok.*
import produvar.interactionwithapi.R

class CustomYesNoDialog(activity: Activity, val message: String, val onYes: () -> Unit) : AppCompatDialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_ok)
        error_message.text = message
        proceed_button.setOnClickListener {
            onYes()
            hide()
        }
    }




}
