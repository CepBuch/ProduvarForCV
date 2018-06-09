package produvar.interactionwithapi.helpers

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import produvar.interactionwithapi.activities.main.MainActivity


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


fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}



//fun Fragment.setUpStatusBar(view: View, color: Int) {
//    val currentActivity = activity
//    if (currentActivity is MainActivity) {
//        view.layoutParams.height = activity?.getStatusBarHeight() ?: 0
//        view.setBackgroundColor(color)
//        if (view.layoutParams.height > 0 && view.visibility != View.VISIBLE) {
//            view.visibility = android.view.View.VISIBLE
//        }
//    }
//}

fun Int.dpToPx(context: Context): Int {
    val r = context.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(), r.displayMetrics).toInt()
}




