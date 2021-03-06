package produvar.interactionwithapi.activities.main.pages

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_page_profile.*
import kotlinx.android.synthetic.main.toolbar_profile.*
import produvar.interactionwithapi.Factory
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.main.pages.authTypes.AuthLoginFragment
import produvar.interactionwithapi.activities.main.pages.authTypes.AuthQrFragment
import produvar.interactionwithapi.enums.LoginType
import produvar.interactionwithapi.helpers.*
import produvar.interactionwithapi.models.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ProfilePageFragment : Fragment(),
        AuthQrFragment.OnQrAuthorizationListener,
        AuthLoginFragment.OnAccountAuthorizationListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_back.setOnClickListener { activity?.onBackPressed() }
        button_logout.setOnClickListener { logOut() }
        setUpTabLayout()
        setUpContent()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onQRAuthComplete(authorizedUser: User) = logIn(authorizedUser)

    override fun onAccountAuthComplete(authorizedUser: User) = logIn(authorizedUser)


    private fun setUpContent() {
        val currentUser = activity?.tryGetCurrentUser()
        if (currentUser != null) {
            showInfoAboutUser(currentUser)
        } else showAuthorizationForm()
    }


    private fun logIn(user: User) {
        activity?.saveUserInfoToPrefs(user)
        showInfoAboutUser(user)
    }

    private fun logOut() {
        activity?.deleteUserInfoFromPrefs()
        showAuthorizationForm()
    }

    private fun showAuthorizationForm() {
        content_authorization.visibility = View.VISIBLE
        content_profile_info.visibility = View.GONE
    }

    private fun showInfoAboutUser(user: User) {
        profile_name.text = if (user.name != null) {
            String.format(getString(R.string.profile_welcome), user.name)
        } else String.format(getString(R.string.profile_welcome), getString(R.string.profile_welcome_qr))

        profile_email.visibility = if (user.username != null) {
            profile_email.text = String.format(getString(R.string.profile_username), user.username)
            View.VISIBLE
        } else View.GONE

        profile_role.visibility = if (user.role != null) {
            profile_role.text = user.role.capitalize()
            View.VISIBLE
        } else View.GONE


        val loginType = if (user.loginType == LoginType.PersonalAccount) {
            getString(R.string.profile_login_type_personal)
        } else {
            getString(R.string.profile_login_type_qr)
        }
        val dateStr = try {
            val format: DateFormat = SimpleDateFormat("MMM, dd | HH:mm", Locale.ENGLISH)
            format.format(user.logoutDate)
        } catch (ex: Exception) {
            null
        }


        profile_login_info.visibility = if (dateStr != null) {
            profile_login_info.text =
                    String.format(getString(R.string.profile_logout_info), dateStr, loginType)
            View.VISIBLE
        } else {
            View.GONE
        }
        content_authorization.visibility = View.GONE
        content_profile_info.visibility = View.VISIBLE
        selectFirstTab()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (!isVisibleToUser) {
            if (view != null /*&& content_authorization.visibility == View.VISIBLE*/) {
                selectFirstTab()
            }
            // Hide soft keyboard if it's shown
            val view = activity?.currentFocus
            if (view != null) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }

    private fun selectFirstTab() {
        if (view != null && auth_tab_layout.selectedTabPosition != 0) {
            auth_tab_layout.getTabAt(0)?.select()
        }
    }

    private fun setUpTabLayout() {
        auth_tab_layout.addTab(auth_tab_layout.newTab().setText(getString(R.string.login_tab_account)), true)
        auth_tab_layout.addTab(auth_tab_layout.newTab().setText(getString(R.string.login_tab_qr_code)))

        auth_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> showLoginFragment()
                        1 -> showQrFragment()
                        else -> showLoginFragment()
                    }
                }
            }
        })
        // post.run() because we have to wait view to lay out.
        // Otherwise when we show AuthLoginFragment() fragment for the first time from onViewCreated(),
        // countTopViewHeight() returns 0. Because views at this moment has height 0
        view?.post {
            showLoginFragment(false)
        }
    }

    private fun showQrFragment() {
        showAuthorizationTypeFragment(AuthQrFragment(), true)
        topViewsColor(ContextCompat.getColor(activity!!, R.color.produvarDarkTransparent))
        view?.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun showLoginFragment(withAnimation: Boolean = true) {
        showAuthorizationTypeFragment(AuthLoginFragment(), false, withAnimation)
        val color = ContextCompat.getColor(activity!!, R.color.produvarOrange)
        topViewsColor(color)
        view?.setBackgroundColor(color)
    }

    private fun topViewsColor(color: Int) {
        toolbar_profile.setBackgroundColor(color)
        auth_tab_layout.setBackgroundColor(color)
    }

    private fun showAuthorizationTypeFragment(fragment: Fragment, swipeForward: Boolean, withAnimation: Boolean = true) {
        val bundle = Bundle()
        bundle.putInt(Constants.PARAM_TOP_VIEWS_HEIGHT, countTopViewHeight())
        fragment.arguments = bundle
        with(childFragmentManager.beginTransaction()) {
            if (withAnimation) {
                setCustomAnimations(
                        if (swipeForward) produvar.interactionwithapi.R.anim.enter_from_right else produvar.interactionwithapi.R.anim.enter_from_left,
                        if (swipeForward) produvar.interactionwithapi.R.anim.exit_to_left else produvar.interactionwithapi.R.anim.exit_to_right)
            }
            replace(produvar.interactionwithapi.R.id.auth_frame_container, fragment)
            commit()
        }
    }

    private fun countTopViewHeight() = toolbar_profile.height + auth_tab_layout.height
}
