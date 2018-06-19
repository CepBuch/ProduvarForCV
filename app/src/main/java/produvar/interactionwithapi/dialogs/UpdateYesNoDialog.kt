package produvar.interactionwithapi.dialogs

import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.Window
import kotlinx.android.synthetic.main.dialog_yes_no.*
import produvar.interactionwithapi.R
import android.graphics.Paint.UNDERLINE_TEXT_FLAG



class UpdateYesNoDialog(activity: Activity, val fromStatus: String, val toStatus: String, val onYes: () -> Unit) : AppCompatDialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_yes_no)
        from_header.paintFlags = from_header.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
        to_header.paintFlags = to_header.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
        from_status.text = fromStatus
        to_status.text = toStatus
        yes_button.setOnClickListener {
            onYes()
            hide()
        }
        no_button.setOnClickListener {
            cancel()
        }
    }


}
