package produvar.interactionwithapi.activities.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import produvar.interactionwithapi.activities.main.pages.MainPageFragment
import produvar.interactionwithapi.activities.main.pages.AuthPageFragment
import produvar.interactionwithapi.activities.main.pages.ScanCameraFragment
import produvar.interactionwithapi.activities.main.pages.ProfilePageFragment
import java.io.Serializable
import com.google.gson.Gson
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.model.UserData


interface SwitchFragmentListener : Serializable {
    fun onSwithToNextFragment()
}


class MainPagerAdapter(val fm: FragmentManager, val activity: AppCompatActivity) : FragmentPagerAdapter(fm) {

    var fragmentAtPos2: Fragment? = null

    val listener = object : SwitchFragmentListener {
        override fun onSwithToNextFragment() {
            fm.beginTransaction().remove(fragmentAtPos2).commitNow()
            fragmentAtPos2 = if (fragmentAtPos2 is AuthPageFragment) {
                ProfilePageFragment.newInstance(this)
            } else {
                AuthPageFragment.newInstance(this)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItem(position: Int) =
            when (position) {
                0 -> ScanCameraFragment.newInstance()
                1 -> MainPageFragment.newInstance()
                2 -> {
                    if (fragmentAtPos2 == null) {
                        val prefs = activity.getPreferences(Context.MODE_PRIVATE)
                        val jsonUserData = prefs.getString(Constants.LOGGED_USER_INFO, null)
                        fragmentAtPos2 = if (jsonUserData == null) {
                            AuthPageFragment.newInstance(listener)
                        } else {
                            ProfilePageFragment.newInstance(listener)
                        }
                    }
                    fragmentAtPos2
                }
                else -> MainPageFragment()
            }

    override fun getItemPosition(`object`: Any): Int {
        return if (`object` is AuthPageFragment && fragmentAtPos2 is ProfilePageFragment) {
            PagerAdapter.POSITION_NONE
        } else if (`object` is ProfilePageFragment && fragmentAtPos2 is AuthPageFragment) {
            PagerAdapter.POSITION_NONE
        } else {
            PagerAdapter.POSITION_UNCHANGED
        }
    }

    override fun getCount() = 3
}
