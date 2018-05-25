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
import kotlinx.android.synthetic.main.fragment_page_authorization.*
import kotlinx.android.synthetic.main.toolbar_profile.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.setUpStatusBar
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import produvar.interactionwithapi.activities.main.pages.authTypes.AuthLoginFragment
import produvar.interactionwithapi.activities.main.pages.authTypes.AuthQrFragment
import produvar.interactionwithapi.model.LoginType
import produvar.interactionwithapi.model.User


class AuthPageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_authorization, container, false)
    }

    lateinit var prefs: SharedPreferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = activity!!.getPreferences(MODE_PRIVATE)

        button_back.setOnClickListener {
            activity?.onBackPressed()
            logIn(User(LoginType.PersonalAccount, "123fdasg123", "cep.buch@gmail.com", "Sergey", "Employee"))
        }

        button_logout.setOnClickListener { logOut() }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setUpStatusBar(status_bar, Color.TRANSPARENT)
        }
        setUpTabLayout()
        setUpContent()
        super.onViewCreated(view, savedInstanceState)
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


    private fun setUpContent() {
        var flag = false
        val userJson = prefs.getString(Constants.LOGGED_USER_INFO, null)
        if (userJson != null) {
            val parsedUser = Gson().fromJson(userJson, User::class.java)
            if (parsedUser != null) {
                flag = true
                showInfoAboutUser(parsedUser)
            }
        }
        if(!flag){
            showAuthorizationForm()
        }
    }

    private fun logIn(user: User) {
        saveUserInfoToPrefs(user)
        showInfoAboutUser(user)
    }

    private fun logOut() {
        saveUserInfoToPrefs(null)
        showAuthorizationForm()
    }

    private fun saveUserInfoToPrefs(user: User?) {
        with(prefs.edit()) {
            val json = Gson().toJson(user)
            putString(Constants.LOGGED_USER_INFO, json)
            apply()
        }
    }

    private fun showAuthorizationForm(){
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

        profile_login_info.text = if (user.loginType == LoginType.PersonalAccount) {
            String.format(getString(R.string.profile_logout_info), getString(R.string.profile_login_type_personal))
        } else {
            String.format(getString(R.string.profile_logout_info), getString(R.string.profile_login_type_qr))
        }

        content_authorization.visibility = View.GONE
        content_profile_info.visibility = View.VISIBLE

//            val sdf = SimpleDateFormat("HH:mm (yyyy-MM-dd)", Locale.ENGLISH)
        // TODO somehow store date of logging in
//            profile_logout_date_info.text = sdf.format(user.loginDate)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        selectFirstTab()
        super.setUserVisibleHint(isVisibleToUser)
    }

    private fun selectFirstTab() {
        if (view != null && auth_tab_layout.selectedTabPosition != 0) {
            auth_tab_layout.getTabAt(0)?.select()
        }
    }

    private fun showQrFragment() {
        showAuthorizationTypeFragment(AuthQrFragment(), true)
        async(UI) {
            bg {
                topViewsColor(ContextCompat.getColor(activity!!, R.color.produvarDarkTransparent))
                view?.setBackgroundColor(Color.TRANSPARENT)
            }.await()
        }
    }

    private fun showLoginFragment(withAnimation: Boolean = true) {
        showAuthorizationTypeFragment(AuthLoginFragment(), false, withAnimation)
        async(UI) {
            bg {
                val color = ContextCompat.getColor(activity!!, R.color.produvarOrange)
                topViewsColor(color)
                view?.setBackgroundColor(color)
            }.await()
        }
    }

    private fun topViewsColor(color: Int) {
        status_bar.setBackgroundColor(color)
        toolbar.setBackgroundColor(color)
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

    private fun countTopViewHeight() = status_bar.height + toolbar.height + auth_tab_layout.height
}
