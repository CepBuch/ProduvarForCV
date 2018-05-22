package produvar.interactionwithapi.activities.permissions

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_permissions.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.changeStatusBarColor


class PermissionsActivity : AppCompatActivity() {
    lateinit var permissionsToAsk: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)
        changeStatusBarColor(R.color.produvarOrange, true)
        permissionsToAsk = intent.extras.getStringArrayList(Constants.PERMISSIONS_TO_ASK)
        showPermissionsInfo()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.REQUEST_PERMISSIONS) {
            showPermissionsInfo()
        }
    }

    private fun showPermissionsInfo() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionsToAsk = permissionsToAsk.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
            if (permissionsToAsk.isEmpty()) {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            } else {
                row_wifi.visibility = if (permissionsToAsk.contains(Manifest.permission.INTERNET)) {
                    View.VISIBLE
                } else View.INVISIBLE
                row_camera.visibility = if (permissionsToAsk.contains(Manifest.permission.CAMERA)) {
                    View.VISIBLE
                } else View.INVISIBLE
                row_nfc.visibility = if (permissionsToAsk.contains(Manifest.permission.NFC)) {
                    View.VISIBLE
                } else View.INVISIBLE

                button_grant.setOnClickListener {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissionsToAsk.toTypedArray(), Constants.REQUEST_PERMISSIONS)
                    }
                }
            }
        }
    }
}
