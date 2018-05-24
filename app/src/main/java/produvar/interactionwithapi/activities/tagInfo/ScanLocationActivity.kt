package produvar.interactionwithapi.activities.tagInfo

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import kotlinx.android.synthetic.main.activity_scan_location.*
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.Constants

class ScanLocationActivity : AppCompatActivity() {

    private lateinit var scanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_location)

        // Getting display metrics when they are available (for fullscreen camera purposes)
        scanner = BarcodeScanner(this, location_camera_preview, { codeScannedCallback(it) })
        button_back.setOnClickListener { onBackPressed() }
    }

    private fun codeScannedCallback(tagInfo: String) {
        val returnIntent = Intent()
        returnIntent.putExtra(Constants.LOCATION_RESULT, tagInfo)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        scanner.setUpAsync()
    }

    override fun onPause() {
        super.onPause()
        scanner.releaseAsync()
    }
}
