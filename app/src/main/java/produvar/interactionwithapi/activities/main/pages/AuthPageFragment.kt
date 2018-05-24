package produvar.interactionwithapi.activities.main.pages

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
import produvar.interactionwithapi.ApiProvider
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.main.SwitchFragmentListener
import produvar.interactionwithapi.activities.main.pages.pofilePageContent.authTypes.AuthLoginFragment
import produvar.interactionwithapi.activities.main.pages.pofilePageContent.authTypes.AuthQrFragment
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.setUpStatusBar
import produvar.interactionwithapi.model.UserData
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson


class AuthPageFragment : Fragment() {

    companion object {
        fun newInstance(logInListener: SwitchFragmentListener): AuthPageFragment {
            val fragment = AuthPageFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.ON_AUTHORIZED_LISTENER, logInListener)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var listener: SwitchFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listener = arguments?.getSerializable(Constants.ON_AUTHORIZED_LISTENER) as SwitchFragmentListener

        button_back.setOnClickListener {
            //            activity?.onBackPressed()
            saveCurrentUser(UserData("adasdasd", "CepBuch", "Sergey", "Loh"))
            listener.onSwithToNextFragment()
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setUpStatusBar(status_bar, Color.TRANSPARENT)
        }

        setUpTabLayout()
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

    private fun saveCurrentUser(userData: UserData) {
        val prefs = activity!!.getPreferences(MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val json = Gson().toJson(userData)
        prefsEditor.putString(Constants.LOGGED_USER_INFO, json)
        prefsEditor.apply()
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
        showFragment(AuthQrFragment(), true)
        async(UI) {
            bg {
                topViewsColor(ContextCompat.getColor(activity!!, R.color.produvarDarkTransparent))
                view?.setBackgroundColor(Color.TRANSPARENT)
            }.await()
        }
    }

    private fun showLoginFragment(withAnimation: Boolean = true) {
        showFragment(AuthLoginFragment(), false, withAnimation)
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

    private fun showFragment(fragment: Fragment, swipeForward: Boolean, withAnimation: Boolean = true) {
        val bundle = Bundle()
        bundle.putInt(Constants.PARAM_TOP_VIEWS_HEIGHT, countTopViewHeight())
        fragment.arguments = bundle
        with(activity!!.supportFragmentManager.beginTransaction()) {
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
