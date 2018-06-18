package produvar.interactionwithapi.activities.main.pages

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_page_main.*
import kotlinx.android.synthetic.main.status_bar_main.*
import kotlinx.android.synthetic.main.toolbar_main_page.*
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.help.HelpActivity
import produvar.interactionwithapi.activities.main.MainActivity

class MainPageFragment : Fragment() {

    interface OnMenuButtonClicked {
        fun onCameraClicked()
        fun onProfileClicked()
    }

    lateinit var callback: OnMenuButtonClicked

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = activity as? OnMenuButtonClicked ?: throw ClassCastException(activity.toString() +
                "must implement OnMenuButtonClicked")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        main_menu_open_camera.setOnClickListener {
            callback.onCameraClicked()
        }
        main_menu_open_profile.setOnClickListener {
            callback.onProfileClicked()
        }

        button_info.setOnClickListener { openHelpActivity() }
        super.onViewCreated(view, savedInstanceState)
    }


    private fun openHelpActivity() {
        val intent = Intent(activity, HelpActivity::class.java)
        // If we're running on Android 5.0 or higher, open activity with "enter from bottom-exit to bottom" animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        } else {
            // Swap without transition animation
            startActivity(intent)
        }
    }

}