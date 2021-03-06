package produvar.interactionwithapi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast

class BarcodeScanner(private val activity: AppCompatActivity,
                     private val cameraPreview: SurfaceView,
                     val onScannedListener: (String) -> Unit,
                     private val previewWidth: Int = 640,
                     private val previewHeight: Int = 480) {

    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var isCameraShown = false


    init {
        setPreviewSize()
    }

    fun setUpAsync() {
        async(UI) {
            bg { setUp() }.await()
        }
    }

    fun releaseAsync() {
        async(UI) {
            bg { release() }.await()
        }
    }

    fun setUp() {
        if (isCameraShown) return
        barcodeDetector = BarcodeDetector.Builder(this.activity).build()
        cameraSource = CameraSource.Builder(this.activity, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedFps(20f)
                .setRequestedPreviewSize(previewWidth, previewHeight)
                .build()

        cameraPreview.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {}

            override fun surfaceCreated(holder: SurfaceHolder?) {
                setPreviewSize()
            }

        })


        barcodeDetector.setProcessor(
                object : Detector.Processor<Barcode> {
                    override fun release() {}
                    override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                        val barcodes: SparseArray<Barcode>? = detections?.detectedItems
                        if (barcodes != null && barcodes.size() > 0) {
                            val bc = barcodes.valueAt(0).displayValue
                            produceVibrationSignal()
                            onScannedListener(bc)
                        }
                    }
                })

        startCamera()
    }

    fun release() {
        if (isCameraShown) {
            barcodeDetector.release()
            cameraSource.release()
            isCameraShown = false
            cameraPreview.holder.setFormat(PixelFormat.TRANSPARENT)
            cameraPreview.holder.setFormat(PixelFormat.OPAQUE)
        }
    }

    private fun startCamera() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(cameraPreview.holder)
                isCameraShown = true
            } else activity.toast("Permission to camera wasn't granted")
        } else {
            cameraSource.start(cameraPreview.holder)
            isCameraShown = true
        }
    }


    fun produceVibrationSignal(milliseconds: Long = 50) {
        val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity.checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } else v.vibrate(milliseconds)
    }

    private fun setPreviewSize() {
        val displayMetrics = activity.resources.displayMetrics ?: return
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val widthIsMax = width > height

        val rectDisplay = RectF()
        rectDisplay.set(0.toFloat(), 0.toFloat(), width.toFloat(), height.toFloat())
        val rectPreview = RectF()
        if (widthIsMax) {
            rectPreview.set(0.toFloat(), 0.toFloat(), previewWidth.toFloat(), previewHeight.toFloat())
        } else {
            rectPreview.set(0.toFloat(), 0.toFloat(), previewHeight.toFloat(), previewWidth.toFloat())
        }

        val matrix = Matrix()
        matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START)
        matrix.invert(matrix)
        matrix.mapRect(rectPreview)

        cameraPreview.layoutParams.height = rectPreview.bottom.toInt()
        cameraPreview.layoutParams.width = rectPreview.right.toInt()

    }

}