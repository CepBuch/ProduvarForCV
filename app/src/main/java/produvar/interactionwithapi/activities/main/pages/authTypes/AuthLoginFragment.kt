package produvar.interactionwithapi.activities.main.pages.authTypes

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_auth_username.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.okButton
import produvar.interactionwithapi.Factory
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.CustomDialog
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.enums.LoginType
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.isConnected
import produvar.interactionwithapi.models.User

class AuthLoginFragment : Fragment() {

    interface OnAccountAuthorizationListener {
        fun onAccountAuthComplete(authorizedUser: User)
    }

    private lateinit var callback: OnAccountAuthorizationListener
    private var customDialog: CustomDialog? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parentFragmentAsCallback = parentFragment as? OnAccountAuthorizationListener
        val activityAsCallback = activity as? OnAccountAuthorizationListener

        callback = when {
            parentFragmentAsCallback != null -> parentFragmentAsCallback
            activityAsCallback != null -> activityAsCallback
            else -> throw ClassCastException("Either activity ($activity) or parent fragment ($parentFragment)" +
                    "  must implement OnQrAuthorizationListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth_username, container, false)

        // centring gravity to the height of mainActivity (not the layout)
        val marginTop = arguments?.getInt(Constants.PARAM_TOP_VIEWS_HEIGHT)
        if (marginTop != null) {
            val params = view.layoutParams as? ViewGroup.MarginLayoutParams
            params?.setMargins(0, -1 * marginTop, 0, 0)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // When user clicks "Done" after entering password
        password.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@setOnEditorActionListener true
            }
            false
        }
        sign_in_button.setOnClickListener { attemptLogin() }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun attemptLogin() {
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()


        if (emailStr.isBlank()) {
            email.error = getString(R.string.login_error_field_required)
            email.requestFocus()
            return
        }
        if (!isValidEmail(emailStr)) {
            email.error = getString(R.string.login_error_invalid_email)
            email.requestFocus()
            return
        }
        if (passwordStr.isBlank()) {
            password.error = getString(R.string.login_error_field_required)
            password.requestFocus()
            return
        }

        launch(UI) {
            showProgress(true)
            if (activity?.isConnected() == true) {
                val provider = Factory.getApiProvider()
                val (user, error) = provider.login(emailStr, passwordStr).await()
                when {
                    user != null -> {
                        val authorizedUser = user.convertToModel(LoginType.PersonalAccount)
                        if (authorizedUser != null) {
                            email.setText("")
                            password.setText("")
                            showProgress(false)
                            callback.onAccountAuthComplete(authorizedUser)
                        } else {
                            showLoginError(ErrorType.UNDEFINED)
                        }
                    }
                    error != null -> showLoginError(error)
                }
            } else {
                showLoginError(ErrorType.NOT_CONNECTED)
            }
        }
    }

    private fun showLoginError(errorType: ErrorType) {
        val message = when (errorType) {
            ErrorType.NOT_FOUND -> getString(R.string.login_personal_not_found_message)
            ErrorType.NOT_CONNECTED -> getString(R.string.error_internet_connection)
            else -> getString(R.string.error_unknown)
        }
        showProgress(false)
        password.setText("")

        customDialog = CustomDialog(activity!!, message) {
            customDialog = null
        }
        customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog?.show()
    }

    override fun onPause() {
        super.onPause()
        showProgress(false)
        customDialog?.hide()
    }

    private fun showProgress(show: Boolean) {
        sign_in_button.visibility = if (show) View.INVISIBLE else View.VISIBLE
        login_progress.visibility = if (show) View.VISIBLE else View.GONE

        if (show) {
            email.isEnabled = false
            password.isEnabled = false

        } else {
            email.isEnabled = true
            password.isEnabled = true
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

