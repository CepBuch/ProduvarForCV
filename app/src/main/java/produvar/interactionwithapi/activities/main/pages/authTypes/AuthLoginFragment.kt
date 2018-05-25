package produvar.interactionwithapi.activities.main.pages.authTypes

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_auth_username.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants

class AuthLoginFragment : Fragment() {

    companion object {
        fun newInstance(): AuthLoginFragment {
            val instance = AuthLoginFragment()
            return instance
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
        // When user clicks "Done" after entering passport
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
            return
        }
        if (!isValidEmail(emailStr)) {
            email.error = getString(R.string.login_error_invalid_email)
            return
        }
        if (passwordStr.isBlank()) {
            password.error = getString(R.string.login_error_field_required)
            return
        }
        async(UI) {
            showProgress(true)
            bg { Thread.sleep(5000) }.await()
            showProgress(false)
        }
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

