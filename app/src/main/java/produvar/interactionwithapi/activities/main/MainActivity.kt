package produvar.interactionwithapi.activities.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import produvar.interactionwithapi.*
import produvar.interactionwithapi.activities.permissions.PermissionsActivity
import produvar.interactionwithapi.helpers.Constants


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Checking whether permissions to use camera/internet/nfc were granted
        checkPermissions()

        // Making status bar transparent on newer devices
        // (it will be given a color in MainPageFragment and ProfileFragment onCreate))
        // this enables to remove statusbar on camera fragment
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        // setting up viewpager
        val mainPagerAdapter = MainPagerAdapter(supportFragmentManager)
        view_pager.adapter = mainPagerAdapter
        view_pager.addOnPageChangeListener(HideStatusBarPageListener(this))
        view_pager.currentItem = savedInstanceState?.getInt("VIEWPAGER_PAGE", 1) ?: 1
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("VIEWPAGER_PAGE", view_pager.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        view_pager.setCurrentItem(1, false)
    }


    override fun onBackPressed() {
        when (view_pager.currentItem) {
            0 -> swipeFragment(true)
            1 -> super.onBackPressed()
            2 -> swipeFragment(false)
        }
    }

    fun swipeFragment(forward: Boolean = true) {
        view_pager.setCurrentItem(view_pager.currentItem + if (forward) 1 else -1, true)
    }

    private fun checkPermissions() {
        // Users with SDK < 23 granted the permission during installation
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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


}







