package produvar.interactionwithapi.activities.main

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) =
            when (position) {
                0 -> ScanCameraFragment()
                1 -> MainPageFragment()
                2 -> ProfileFragment()
                else -> MainPageFragment()
            }

    override fun getCount() = 3
}

class HideStatusBarPageListener(val activity: AppCompatActivity) : ViewPager.SimpleOnPageChangeListener() {
    override fun onPageSelected(position: Int) {
        val decorView = activity.window.decorView
        val uiOptions = if (position == 0) {
            View.SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        decorView.systemUiVisibility = uiOptions
        super.onPageSelected(position)
    }
}