package kr.co.gooroomeelite.views.statistics.share

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.*
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kr.co.gooroomeelite.databinding.ActivityShareBinding
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

//    private var isFlashEnabled: Boolean = false

    private val displayManager by lazy{
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

//    private var cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_BACK).build()
//    private var mCameraID = LENS_BACK

    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

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
//        initToolBar()
        binding.picturePrevious.setOnClickListener{
            finish()
        }
        binding.pictureNext.setOnClickListener{
            val nextIntent = Intent(this, StickerActivity::class.java)
            startActivity(nextIntent)
        }
        binding.showImage.setOnClickListener{ openGallery() }
        binding.rotateBtn.setOnClickListener{
            swicthCamera()
        }
//        binding.converterCamera.setOnClickListener{ swicthCamera() }
        startCamera(binding.viewFinder)
    }

    //카메라 전환(앞면/후면)
    private fun swicthCamera() {
        if(lensFacing == CameraSelector.DEFAULT_BACK_CAMERA){
            lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
        }else if(lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA){
            lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
        }
//        startCamera(binding.viewFinder)
        bindCameraUseCase()
    }
//      툴바 뒤로가기
//    private fun initToolBar() {
//        val toolbar = binding.pictureToolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)//뒤로 버튼
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.,menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> { finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //갤러리 앱 화면에 띄우기
    private val OPEN_GALLERY : Int = 1
    private fun openGallery(){
        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                var currentImageUrl : Uri? = data?.data
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageUrl)
                    val galleryIntent = Intent(this@ShareActivity,StickerActivity::class.java)
                    Log.d("aaaacurrentImageUrl", currentImageUrl.toString())
                    galleryIntent.putExtra("gallery",currentImageUrl.toString())
                    startActivity(galleryIntent)
//                    binding.showImageView.setImageBitmap(bitmap)
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }else{
                Log.d("aaaa","something wrong")
            }
        }
    }

    private fun startCamera(viewFinder : PreviewView){
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
                camera = cameraProvider.bindToLifecycle(this@ShareActivity,lensFacing,preview,imageCapture)
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
                bindCaptureListener()
                bindZoomListner()
//                initFlashAndAddListener()
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

//    private fun initFlashAndAddListener() = with(binding){
//        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
//        flashSwitch.isGone = hasFlash.not()
//        if(hasFlash){
//            flashSwitch.setOnCheckedChangeListener { _, isChecked ->
//                isFlashEnabled = isChecked
//            }
//        }else{
//            isFlashEnabled = false
//            flashSwitch.setOnClickListener(null)
//        }
//    }



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
//        if(isFlashEnabled) flashLight(true)
        imageCapture.takePicture(outputOptions,cameraExcutor,object:ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                contentUri = savedUri //저장된 Uri를 넣어준다.
                updateSavedImageContent()
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                isCapturing = false
//                flashLight(false)
            }

        })
    }
//    private fun flashLight(light:Boolean){
//        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
//        if(hasFlash){
//            camera?.cameraControl?.enableTorch(light)
//        }
//    }

    private fun updateSavedImageContent() {
        contentUri?.let{
            isCapturing = try{
                val file = File(PathUtil.getPath(this,it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this,arrayOf(file.path),arrayOf("image/jpeg"),null)

                val stickerIntent = Intent(this@ShareActivity,StickerActivity::class.java)
                stickerIntent.putExtra("picture",contentUri.toString())
                Log.d("aaaa",contentUri.toString())
                startActivity(stickerIntent)
//                flashLight(false)
                false
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this,"파일이 존재하지 않습니다.",Toast.LENGTH_SHORT).show()
//                flashLight(false)
                false
            }
        }
    }
    companion object{
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}