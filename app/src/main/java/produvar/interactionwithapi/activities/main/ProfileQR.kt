package produvar.interactionwithapi.activities.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile_qr.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R


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
            mainActivity.toast("$it found")
        })

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


