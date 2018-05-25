package produvar.interactionwithapi.activities.main.pages.authTypes

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_auth_qr.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.TagChecker


class AuthQrFragment : Fragment() {

    lateinit var scanner: BarcodeScanner
    lateinit var mainActivity: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth_qr, container, false)

        // centring gravity to the height of mainActivity (not the layout)
        val marginTop = arguments?.getInt(Constants.PARAM_TOP_VIEWS_HEIGHT)
        if (marginTop != null) {
            val params = view.layoutParams as? ViewGroup.MarginLayoutParams
            params?.setMargins(0, -1 * marginTop, 0, 0)
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = activity as? MainActivity ?: throw Exception("Fragment is strongly " +
                "coupled with MainActivity. You can create it in only in MainActivity.")

        scanner = BarcodeScanner(mainActivity, mainActivity.camera_preview, {
            async(UI) {
                showProgress(true)
                bg {
                    scanner.releaseAsync()
                    Thread.sleep(5000)
                    scanner.setUpAsync()
                }.await()
                showProgress(false)
            }
        })

        scanner.setUpAsync()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun attemptLogin(barcode: String) {
        if (!TagChecker.isAuthorizationTagValid(barcode)) {
            return
        }

    }


    override fun onPause() {
        super.onPause()
        scanner.releaseAsync()
    }

    override fun onResume() {
        super.onResume()
        view?.post {
            scanner.setUpAsync()
        }
    }


    private fun showProgress(show: Boolean) {
        rectangleView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
    }
}


