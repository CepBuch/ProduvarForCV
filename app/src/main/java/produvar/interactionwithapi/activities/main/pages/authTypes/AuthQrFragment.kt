package produvar.interactionwithapi.activities.main.pages.authTypes


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_auth_qr.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.Factory
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.CustomDialog
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.enums.LoginType
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.isConnected
import produvar.interactionwithapi.models.User


class AuthQrFragment : Fragment() {

    interface OnQrAuthorizationListener {
        fun onQRAuthComplete(authorizedUser: User)
    }

    private lateinit var callback: OnQrAuthorizationListener
    private lateinit var scanner: BarcodeScanner
    private lateinit var mainActivity: MainActivity
    private var customDialog: CustomDialog? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parentFragmentAsCallback = parentFragment as? OnQrAuthorizationListener
        val activityAsCallback = activity as? OnQrAuthorizationListener

        callback = when {
            parentFragmentAsCallback != null -> parentFragmentAsCallback
            activityAsCallback != null -> activityAsCallback
            else -> throw ClassCastException("Either activity ($activity) or parent fragment ($parentFragment)" +
                    "  must implement OnQrAuthorizationListener")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth_qr, container, false)

        // centring gravity to the height of parent fragment layout (not the one of current fragment)
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
            launch(UI) {
                scanner.release()
                showProgress(true)
                runBlocking { attemptLogin(it) }
            }
        })
        scanner.setUpAsync()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun attemptLogin(barcode: String) {
        if (activity?.isConnected() == true) {
            val provider = Factory.getApiProvider()
            provider.authenticate(barcode,
                    {
                        val authorizedUser = it.convertToModel(LoginType.QR)
                        if (authorizedUser != null) {
                            callback.onQRAuthComplete(authorizedUser)
                        } else {
                            showLoginError(ErrorType.UNDEFINED)
                        }
                    },
                    {
                        showLoginError(it)
                    })
        } else {
            showLoginError(ErrorType.NOT_CONNECTED)
        }
    }


    private fun showLoginError(errorType: ErrorType) {
        val message = when (errorType) {
            ErrorType.NOT_FOUND -> getString(R.string.login_qr_not_found_message)
            ErrorType.NOT_CONNECTED -> getString(R.string.error_internet_connection)
            else -> getString(R.string.error_unknown)
        }
        showProgress(false)
        if (customDialog?.isShowing != true) {
            customDialog = CustomDialog(mainActivity, message) {
                scanner.setUpAsync()
                customDialog = null
            }
            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialog?.show()
        }
    }

    override fun onPause() {
        super.onPause()
        showProgress(false)
        customDialog?.hide()
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


