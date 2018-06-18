package produvar.interactionwithapi.activities.main.pages

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_page_camera.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import produvar.interactionwithapi.BarcodeScanner
import produvar.interactionwithapi.R
import produvar.interactionwithapi.activities.CustomDialog
import produvar.interactionwithapi.activities.main.MainActivity
import produvar.interactionwithapi.activities.tagInfo.TagInfoActivity
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.helpers.isConnected


class ScanCameraFragment : Fragment() {

    private lateinit var scanner: BarcodeScanner
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainActivity = activity as? MainActivity ?: throw Exception("Fragment is strongly " +
                "coupled with MainActivity and can be used only in it.")

        button_back.setOnClickListener { activity?.onBackPressed() }

        scanner = BarcodeScanner(mainActivity, mainActivity.camera_preview, {
            launch(UI) {
                scanner.release()
                tryProcessTag(it)
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    private fun tryProcessTag(tagContent: String) {
        val intent = Intent(activity, TagInfoActivity::class.java)
        intent.putExtra("barcode", tagContent)
        mainActivity.startActivity(intent)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (view != null) {
            if (isVisibleToUser) {
                scanner.setUpAsync()
            } else scanner.release()
        }
    }

    override fun onPause() {
        super.onPause()
        scanner.release()
    }


}
