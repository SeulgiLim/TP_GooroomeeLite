package kr.co.gooroomeelite.views.statistics.share

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.camera2.*
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import kr.co.gooroomeelite.databinding.ActivityShareBinding
import kr.co.gooroomeelite.views.statistics.share.extensions.loadCenterCrop
import kr.co.gooroomeelite.views.statistics.share.util.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareBinding
    private lateinit var cameraExcutor :  ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private lateinit var imageCapture: ImageCapture

    private var camera: Camera? = null
    private var root: View? = null

    private var isCapturing : Boolean = false

    private var isFlashEnabled: Boolean = false


    private val displayManager by lazy{
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private var displayId : Int = 1
    private val displayListener = object: DisplayManager.DisplayListener{
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int) = Unit

        override fun onDisplayChanged(displayId: Int) {
            if(this@ShareActivity.displayId == displayId) {
                if (::imageCapture.isInitialized && root != null) {
                    imageCapture.targetRotation = root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(binding.root)

        startCamera(binding.viewFinder)
    }

    private fun startCamera(viewFinder : PreviewView){ //어떤 걸 넘겨줄 것인가?
        displayManager.registerDisplayListener(displayListener,null)
        cameraExcutor = Executors.newSingleThreadExecutor()
        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        },10)
    }

    private fun bindCameraUseCase() = with(binding){
        //화면 회전에 대해 체크
        val rotation = viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_BACK).build()
        val cameraSelectorFront = CameraSelector.Builder().requireLensFacing(LENS_FRONT).build()
        val selector : Int = 0


        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply{
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
            }.build()

            val imageCaptureBuilder = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            imageCapture = imageCaptureBuilder.build()

            try{
                cameraProvider.unbindAll()
                binding.btnConvert.setOnClickListener {
                    camera = when (selector) {
                        1 -> cameraProvider.bindToLifecycle(this@ShareActivity, cameraSelector, preview, imageCapture )
                        else -> cameraProvider.bindToLifecycle(this@ShareActivity, cameraSelectorFront, preview, imageCapture )
                    }
                }
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
                bindCaptureListener()
                bindZoomListner()
                initFlashAndAddListener()
            }catch(e:Exception){
                e.printStackTrace()
            }
        },cameraMainExecutor)
    }

    private fun bindCaptureListener() = with(binding){
        captureButton.setOnClickListener{
            //클릭하는 시점에 현재 캡처 중인지 아닌지 체크
            if(isCapturing.not()){
                isCapturing = true
                captureCamera()
            }
        }
    }

    private fun bindZoomListner() = with(binding){
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector?.scaleFactor
                   camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta!!)
                return true//콜백 메서드
            }
        }
        @SuppressLint("ClickableViewAccessibility")
        val scaleGestureDetector = ScaleGestureDetector(this@ShareActivity,listener)
        viewFinder.setOnTouchListener{_,event->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private fun initFlashAndAddListener() = with(binding){
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        flashSwitch.isGone = hasFlash.not()
        if(hasFlash){
            flashSwitch.setOnCheckedChangeListener { _, isChecked ->
                isFlashEnabled = isChecked
            }
        }else{
            isFlashEnabled = false
            flashSwitch.setOnClickListener(null)
        }
    }



    private var contentUri : Uri? = null
    private fun captureCamera(){
        if(::imageCapture.isInitialized.not()) return
        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        if(isFlashEnabled) flashLight(true)
        imageCapture.takePicture(outputOptions,cameraExcutor,object:ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                contentUri = savedUri //저장된 Uri를 넣어준다.
                updateSavedImageContent()

            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                isCapturing = false
                flashLight(false)
            }

        })
    }
    private fun flashLight(light:Boolean){
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        if(hasFlash){
            camera?.cameraControl?.enableTorch(light)
        }
    }

    private fun updateSavedImageContent() {
        contentUri?.let{
            Log.d("aaaa",it.toString())
            isCapturing = try{
                val file = File(PathUtil.getPath(this,it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this,arrayOf(file.path),arrayOf("image/jpeg"),null)
                Handler(Looper.getMainLooper()).post{
//                    binding.previewImageVIew.loadCenterCrop(url = it.toString(), corner = 4f)//현재 메인쓰레드에서 이미지를 처리해줄 수 있도록 한다.
                }
                val stickerIntent = Intent(this@ShareActivity,StickerActivity::class.java)
                stickerIntent.putExtra("picture",contentUri.toString())
                startActivity(stickerIntent)
                flashLight(false)
                false
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this,"파일이 존재하지 않습니다.",Toast.LENGTH_SHORT).show()
                flashLight(false)
                false
            }
        }
    }
    companion object{
        private val LENS_BACK: Int = CameraSelector.LENS_FACING_BACK
        private val LENS_FRONT: Int = CameraSelector.LENS_FACING_FRONT
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}