package produvar.interactionwithapi.activities.main

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile_qr.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants


class ProfileAuthQR : Fragment() {

    lateinit var scanner: BarcodeScanner
    lateinit var mainActivity: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_qr, container, false)

        // centring gravity to the height of mainActivity (not the layout)
        val marginTop = arguments?.getInt(Constants.PARAM_TOP_VIEWS_HEIGHT)
        if (marginTop != null) {
            val params = view.layoutParams as? ViewGroup.MarginLayoutParams
            params?.setMargins(0, -1 * marginTop, 0, 0)
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        } else return

        scanner = BarcodeScanner(mainActivity, mainActivity.camera_preview, {
            mainActivity.toast("$it found")

        })

//        Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                .setAction(android.R.string.ok,
//                        {  })

        scanner.setUpAsync()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onPause() {
        super.onPause()
        scanner.releaseAsync()
    }

    override fun onResume() {
        super.onResume()
        scanner.setUpAsync()
    }
}


