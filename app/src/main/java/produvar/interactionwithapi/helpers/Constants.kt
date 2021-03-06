package produvar.interactionwithapi.helpers

import android.Manifest

class Constants {
    companion object {
        const val REQUEST_PERMISSIONS = 0
        const val PARAM_TOP_VIEWS_HEIGHT = "TOP_VIEWS_HEIGHT"
        const val PERMISSIONS_TO_ASK = "PERMISSIONS_TO_ASK"
        const val SCAN_LOCATION = 1
        const val LOCATION_RESULT = "location_result"
        const val TAGINFO_ACTIVITY = 2
        const val PREFS_FILE_NAME = "produvar_prefs"
        const val LOGGED_USER_INFO = "LOGGED_USER_INFO"
        val IMPORTANT_PERMISSIONS = arrayOf(Manifest.permission.INTERNET,
                Manifest.permission.CAMERA, Manifest.permission.NFC)
    }
}