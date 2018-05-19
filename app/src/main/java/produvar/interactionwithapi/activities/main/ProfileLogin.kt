package produvar.interactionwithapi.activities.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import produvar.interactionwithapi.R

class ProfileLogin : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sign_in_button.setOnClickListener{
            async(UI){
                it.visibility = View.GONE
                bg{Thread.sleep(5000)}.await()
                it.visibility = View.VISIBLE
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}

