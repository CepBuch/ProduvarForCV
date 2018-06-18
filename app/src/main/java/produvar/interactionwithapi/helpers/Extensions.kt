package produvar.interactionwithapi.helpers

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.gson.Gson
import org.jetbrains.anko.longToast
import produvar.interactionwithapi.Factory
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.models.User


fun Activity.changeStatusBarColor(colorResource: Int, shouldSetFlags: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (shouldSetFlags) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        val window = window
        window.statusBarColor = ContextCompat.getColor(this, colorResource)
    }
}


fun Context.isConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}

fun Activity.tryGetCurrentUser(isLoggingOut: Boolean = false): User? {
    val prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, MODE_PRIVATE)
    val userJson = prefs.getString(Constants.LOGGED_USER_INFO, null)
    if (userJson != null) {
        val user = Gson().fromJson(userJson, User::class.java)
        if (user != null) {
            if (!isLoggingOut) {
                if (!user.isTokenExpired()) {
                    return user
                } else {
                    this.longToast(getString(R.string.profile_authorization_expired))
                    deleteUserInfoFromPrefs()
                }
            } else {
                return user
            }
        }
    }
    return null
}


fun Activity.saveUserInfoToPrefs(user: User) {
    val prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, MODE_PRIVATE)
    with(prefs.edit()) {
        val json = com.google.gson.Gson().toJson(user)
        putString(produvar.interactionwithapi.helpers.Constants.LOGGED_USER_INFO, json)
        apply()
    }
}

fun Activity.deleteUserInfoFromPrefs() {
    val prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, MODE_PRIVATE)
    val currentUser = tryGetCurrentUser(true)
    if (currentUser != null) {
        if (isConnected()) {
            val provider = Factory.getApiProvider()
            provider.logout(currentUser)
        }
    }
    with(prefs.edit()) {
        remove(Constants.LOGGED_USER_INFO)
        apply()
    }
}




