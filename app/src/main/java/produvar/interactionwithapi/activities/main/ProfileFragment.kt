package produvar.interactionwithapi.activities.main

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabItem
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar_profile.*
import org.jetbrains.anko.alert
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.setUpStatusBar


class ProfileFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = activity as? MainActivity ?: throw Exception("Fragment is strongly " +
                "coupled with MainActivity. You can create it in only in MainActivity.")

        button_back.setOnClickListener { mainActivity.swipeFragment(false) }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setUpStatusBar(status_bar, Color.TRANSPARENT)
        }
        setUpTabLayout()

//        view.post { view.height }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpTabLayout() {
        profile_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        // Otherwise when we show ProfileLogin() fragment for the first time from onViewCreated(),
        // countTopViewHeight() returns 0. Because views at this moment has height 0
        view?.post{
            profile_tab_layout.addTab(profile_tab_layout.newTab().setText(getString(R.string.profile_tab_account)), true)
            profile_tab_layout.addTab(profile_tab_layout.newTab().setText(getString(R.string.profile_tab_qr_code)))
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        selectFirstTab()
        super.setUserVisibleHint(isVisibleToUser)
    }

    private fun selectFirstTab() {
        if (view != null) {
            profile_tab_layout.getTabAt(0)?.select()
        }
    }

    private fun showLoginFragment() {
        showFragment(ProfileLogin())
        val color = ContextCompat.getColor(mainActivity, R.color.produvarOrange)
        topViewsColor(color)
        view?.setBackgroundColor(color)


    }

    private fun showQrFragment() {
        showFragment(ProfileQR())
        topViewsColor(ContextCompat.getColor(mainActivity, R.color.produvarDarkTransparent))
        view?.setBackgroundColor(Color.TRANSPARENT)

    }

    private fun topViewsColor(color: Int) {
        status_bar.setBackgroundColor(color)
        toolbar.setBackgroundColor(color)
        profile_tab_layout.setBackgroundColor(color)
    }


    private fun showFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putInt(Constants.PARAM_TOP_VIEWS_HEIGHT, countTopViewHeight())
        fragment.arguments = bundle
        val fm = mainActivity.supportFragmentManager
        val ft = fm.beginTransaction()
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.frame_container, fragment)
        ft.commit()
    }

    private fun countTopViewHeight(): Int {
        return status_bar.height + toolbar.height + profile_tab_layout.height
    }


}
