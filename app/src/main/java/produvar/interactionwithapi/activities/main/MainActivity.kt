package produvar.interactionwithapi.activities.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import produvar.interactionwithapi.*
import produvar.interactionwithapi.activities.main.pages.ProfilePageFragment
import produvar.interactionwithapi.activities.main.pages.MainPageFragment
import produvar.interactionwithapi.activities.main.pages.ScanCameraFragment
import produvar.interactionwithapi.activities.permissions.PermissionsActivity
import produvar.interactionwithapi.enums.LoginType
import produvar.interactionwithapi.enums.TagType
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.tryGetCurrentUser
import produvar.interactionwithapi.models.User
import produvar.interactionwithapi.models.UserDTO


class MainActivity : AppCompatActivity(), MainPageFragment.OnMenuButtonClicked {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Checking whether permissions to use camera/internet/nfc were granted
        checkPermissions()
        setUpViewPager()
    }


    private fun setUpViewPager() {
        view_pager.currentItem = 1
        view_pager.offscreenPageLimit = 3
        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int) =
                    when (position) {
                        0 -> ScanCameraFragment()
                        1 -> MainPageFragment()
                        2 -> ProfilePageFragment()
                        else -> MainPageFragment()
                    }

            override fun getCount() = 3
        }
//        view_pager.addOnPageChangeListener(
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//
//                    object : ViewPager.SimpleOnPageChangeListener() {
//                        override fun onPageSelected(position: Int) {
//                            changeStatusBarColor(when (position) {
//                                0 -> Color.TRANSPARENT
//                                1 -> ContextCompat.getColor(this@MainActivity, R.color.statusbarMain)
//                                2 -> ContextCompat.getColor(this@MainActivity, R.color.produvarOrange)
//                                else -> ContextCompat.getColor(this@MainActivity, R.color.produvarDark)
//                            })
//                            super.onPageSelected(position)
//                        }
//                    }
//                } else {
//                object : ViewPager.SimpleOnPageChangeListener() {
//                    override fun onPageSelected(position: Int) {
//                        val decorView = window.decorView
//                        val uiOptions = if (position == 0) {
//                            View.SYSTEM_UI_FLAG_FULLSCREEN
//                        } else {
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        }
//                        decorView.systemUiVisibility = uiOptions
//                        super.onPageSelected(position)
//                    }
//                }
//                }
//        )


    }

    private fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    override fun onResume() {
        super.onResume()
        if (view_pager.currentItem == 0) {
            view_pager.setCurrentItem(1, false)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Constants.TAGINFO_ACTIVITY){
            recreate()
        }
    }

    override fun onBackPressed() {
        when (view_pager.currentItem) {
            0 -> swipeFragment(true)
            1 -> super.onBackPressed()
            2 -> swipeFragment(false)
        }
    }

    private fun swipeFragment(forward: Boolean = true) {
        view_pager.setCurrentItem(view_pager.currentItem + if (forward) 1 else -1, true)
    }

    override fun onProfileClicked() = swipeFragment(true)

    override fun onCameraClicked() = swipeFragment(false)

    private fun checkPermissions() {
        // Users with SDK < 23 granted the permission during installation
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val permissionsToAsk = ArrayList(Constants.IMPORTANT_PERMISSIONS
                .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED })

        if (permissionsToAsk.isNotEmpty()) {
            val permissionsIntent = Intent(this, PermissionsActivity::class.java)
            permissionsIntent.putStringArrayListExtra(Constants.PERMISSIONS_TO_ASK, permissionsToAsk)
            startActivity(permissionsIntent)
            finish()
        }

    }

}







