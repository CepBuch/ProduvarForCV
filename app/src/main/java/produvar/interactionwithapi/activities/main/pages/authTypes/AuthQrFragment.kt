package produvar.interactionwithapi.activities.main.pages.authTypes

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_auth_qr.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AlertBuilder
import org.jetbrains.anko.alert
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.okButton
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.TagChecker
import produvar.interactionwithapi.model.LoginType
import produvar.interactionwithapi.model.User


class AuthQrFragment : Fragment() {

    interface OnQrAuthorizationListener {
        fun authorizationComplete(authorizedUser: User)
    }

    private var listener: OnQrAuthorizationListener? = null
    private lateinit var scanner: BarcodeScanner
    private lateinit var mainActivity: MainActivity

    private var dialog: DialogInterface? = null

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
                attemptLogin(it)
            }
        })

        scanner.setUpAsync()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (parentFragment is OnQrAuthorizationListener) {
            listener = parentFragment as OnQrAuthorizationListener
        } else {
            throw RuntimeException("$context must implement OnQrAuthorizationListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun attemptLogin(barcode: String) {
        showProgress(true)
        scanner.release()

        if (!TagChecker.isAuthorizationTagValid(barcode)) {
            showLoginError(getString(R.string.login_qr_wrong_format_title),
                    getString(R.string.login_qr_wrong_format_message))
            return
        }

        async(UI) {
            val authorizedUser = bg {
                // API CALL SIMULATION
                Thread.sleep(1000)
                if (barcode == "http://produvar.nl") User("mb.M8m_^GIol@YT|") else null
            }.await()

            if (authorizedUser != null) {
                listener?.authorizationComplete(authorizedUser)
            } else {
                showLoginError(getString(R.string.login_qr_not_found_title),
                        getString(R.string.login_qr_not_found_message))
            }
        }
    }


    private fun showLoginError(title: String, message: String) {
        dialog = activity!!.alert(message,title) {
            okButton {
                scanner.setUpAsync()
                showProgress(false)
            }
        }.show()
    }

    override fun onPause() {
        super.onPause()
        if (dialog != null) {
            val a = dialog?.dismiss()
            showProgress(false)
        }
        scanner.releaseAsync()
    }

    override fun onResume() {
        super.onResume()
        scanner.setUpAsync()
    }

    private fun showProgress(show: Boolean) {
        rectangleView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
    }
}


