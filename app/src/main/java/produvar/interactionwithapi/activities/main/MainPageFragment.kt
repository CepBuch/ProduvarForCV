package produvar.interactionwithapi.activities.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.status_bar_main.*
import kotlinx.android.synthetic.main.toolbar_main_page.*
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.R
import produvar.interactionwithapi.helpers.setUpStatusBar

class MainPageFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainActivity = activity as? MainActivity ?: throw Exception("Fragment is strongly " +
                "coupled with MainActivity. You can create it in only in MainActivity.")

        main_menu_open_camera.setOnClickListener {
            mainActivity.swipeFragment(false)
        }
        main_menu_open_profile.setOnClickListener {
            mainActivity.swipeFragment()
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setUpStatusBar(status_bar, ContextCompat.getColor(mainActivity, R.color.statusbarMain))
        }


        super.onViewCreated(view, savedInstanceState)
    }

}