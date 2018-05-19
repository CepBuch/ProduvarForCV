package produvar.interactionwithapi.helpers

import android.Manifest

class Constants {
    companion object {
        const val REQUEST_PERMISSIONS = 0
        const val PERMISSIONS_TO_ASK = "PERMISSIONS_TO_ASK"
        const val REQUEST_CAMERA = 10
        val IMPORTANT_PERMISSIONS = arrayOf(Manifest.permission.INTERNET,
                Manifest.permission.CAMERA, Manifest.permission.NFC)
    }
}