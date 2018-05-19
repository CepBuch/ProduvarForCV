package produvar.interactionwithapi.activities.main

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar_profile.*
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.setUpStatusBar


class ProfileFragment : Fragment() {
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (activity is MainActivity) {
            mainActivity = activity as MainActivity
        } else return

        button_back.setOnClickListener { mainActivity.swipeFragment(false) }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setUpStatusBar(status_bar, Color.TRANSPARENT)
        }

        setUpTabLayout()

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
        showLoginFragment()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        selectFirstTab()
    }

    override fun onPause() {
        super.onPause()
        selectFirstTab()
    }

    private fun selectFirstTab() {
        if (view != null && profile_tab_layout.selectedTabPosition != 0) {
            profile_tab_layout.getTabAt(0)?.select()
        }
    }

    private fun showLoginFragment() {
        val color = ContextCompat.getColor(mainActivity, R.color.produvarOrange)
        view?.setBackgroundColor(color)
        topViewsColor(color)
        showFragment(ProfileLogin())
    }

    private fun showQrFragment() {
        view?.setBackgroundColor(Color.TRANSPARENT)
        topViewsColor(ContextCompat.getColor(mainActivity, R.color.produvarDarkTransparent))
        showFragment(ProfileQR())
    }

    private fun topViewsColor(color: Int) {
        status_bar.setBackgroundColor(color)
        toolbar.setBackgroundColor(color)
        profile_tab_layout.setBackgroundColor(color)
    }


    private fun showFragment(fragment: Fragment) {
        val fm = mainActivity.supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frame_container, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }


}
