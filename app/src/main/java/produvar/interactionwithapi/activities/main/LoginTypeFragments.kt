package produvar.interactionwithapi.activities.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile_qr.*
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.toast


class ProfileQR : Fragment() {

    lateinit var scanner: BarcodeScanner
    lateinit var mainActivity: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        } else return

        scanner = BarcodeScanner(mainActivity, mainActivity.camera_preview, {
            mainActivity.toast("$it was found")
        })
        scanner.setUp()
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onPause() {
        super.onPause()
        scanner.release()
    }

    override fun onResume() {
        super.onResume()
        scanner.setUp()
    }
}

class ProfileLogin : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}




