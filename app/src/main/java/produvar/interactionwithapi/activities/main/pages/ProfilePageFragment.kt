package produvar.interactionwithapi.activities.main.pages

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_page_authorization.*
import kotlinx.android.synthetic.main.toolbar_profile.*
import org.jetbrains.anko.toast
import produvar.interactionwithapi.ApiProvider
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.main.SwitchFragmentListener
import produvar.interactionwithapi.helpers.Constants
import produvar.interactionwithapi.helpers.setUpStatusBar
import produvar.interactionwithapi.model.UserData

class ProfilePageFragment : Fragment() {
    companion object {
        fun newInstance(logOutListener: SwitchFragmentListener): ProfilePageFragment {
            val fragment = ProfilePageFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.ON_LOG_OUT_LISTENER, logOutListener)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var listener: SwitchFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listener = arguments?.getSerializable(Constants.ON_LOG_OUT_LISTENER) as SwitchFragmentListener

        button_back.setOnClickListener {
            logOut()
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setUpStatusBar(status_bar, ContextCompat.getColor(activity!!, R.color.produvarOrange))
        }

        showInfoAboutUser()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showInfoAboutUser() {
        val prefs = activity!!.getPreferences(Context.MODE_PRIVATE)
        val jsonUserData = prefs.getString(Constants.LOGGED_USER_INFO, null)
        val userData = Gson().fromJson(jsonUserData, UserData::class.java)
        if (userData == null) {
            listener.onSwithToNextFragment()
        }
    }


    private fun logOut() {
        val prefs = activity!!.getPreferences(Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val json = Gson().toJson(null)
        prefsEditor.putString(Constants.LOGGED_USER_INFO, json)
        prefsEditor.apply()
        listener.onSwithToNextFragment()
    }


}
