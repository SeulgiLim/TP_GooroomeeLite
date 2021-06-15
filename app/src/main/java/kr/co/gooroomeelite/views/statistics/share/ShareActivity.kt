package kr.co.gooroomeelite.views.statistics.share

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.*
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseIntArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import kr.co.gooroomeelite.R
import java.util.jar.Manifest
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.exifinterface.media.ExifInterface
import aop.fastcampus.part5.chapter03.util.PathUtil
import kotlinx.android.synthetic.main.activity_share.*
import kr.co.gooroomeelite.databinding.ActivityShareBinding
import splitties.toast.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareActivity : AppCompatActivity() {
    private lateinit var mSurfaceViewHolder: SurfaceHolder

    private lateinit var mAccelerometer: Sensor //가속도계
    private lateinit var mMagnetometer: Sensor  //자력계
    private lateinit var mSensorManager: SensorManager //센서관리자

    private lateinit var mImageReader: ImageReader //사진 형식 저장
    private lateinit var mCameraDevice: CameraDevice //카메라 크기

    private lateinit var mPreviewBuilder: CaptureRequest.Builder //프리뷰를 계속해서 보여주기 위한 핸들러이다.
    private lateinit var mSession: CameraCaptureSession

    private val deviceOrientation: DeviceOrientation by lazy { DeviceOrientation() }


    //스마트폰 크기 호환성
    private var mHeight: Int = 0
    private var mWidth: Int = 0


    //백그라운드 스레드
    private var mHandler: Handler? = null

    var mCameraId = CAMERA_BACK

    //Camera Config
    private lateinit var binding: ActivityShareBinding
    private var root: View? = null


    companion object {
        const val CAMERA_BACK = "0"
        const val CAMERA_FRONT = "1"

        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(ExifInterface.ORIENTATION_NORMAL, 0)
            ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_90, 90)
            ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_180, 180)
            ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_270, 270)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //상태바를 안보이도록 한다.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //화면이 켜진 상태를 유지한다.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        )
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        root = binding.root
//        setContentView(R.layout.activity_share)

        initSensor()
        initView()
    }

    private lateinit var imageCapture: ImageCapture
    private var isCapturing: Boolean = false

    private var contentUri: Uri? = null
    //카메라 캡처 기능
    private fun captureCamera() {
        if (!::imageCapture.isInitialized) return
        val photoFile = File(//어떤 위치에다가 파일을 쓸지??
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build() //파일 객체를 지정해서 빌드를 할 것이다.

        if (isFlashEnabled) flashLight(true)
        imageCapture.takePicture(
            outputOptions, //파일 위치 지정
            cameraExecutor, //카메라 촬영 후 저장할 수 있음
            object : ImageCapture.OnImageSavedCallback { //이미지가 저장하는 시점에 uri를 받아다가 uri를 기반으로 이미지를 다른 갤러리로 볼 수 있도록 업데이트를 해준다.
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    val rotation = binding.surfaceView.display.rotation // 회전 값 설정
                    contentUri = savedUri
                    updateSavedImageContent()
                }

                override fun onError(e: ImageCaptureException) { //에러가 나는 경우에 대응을 해준다.
                    e.printStackTrace()
                    isCapturing = false
                }
            })

    }

    private fun updateSavedImageContent() {
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)
                Handler(Looper.getMainLooper()).post {
                    binding.previewImageVIew.loadCenterCrop(url = it.toString(), corner = 4f)
                }
                if (isFlashEnabled) flashLight(false)
                uriList.add(it)
                false
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun initSensor() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) //가속도계
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) //자력계

    }

    private fun initView() {
        //스마트폰 크기 호환성 확인!! 없어도 되는 코드
        with(DisplayMetrics()) {
            windowManager.defaultDisplay.getMetrics(this)
            mHeight = heightPixels
            mWidth = widthPixels
        }

//        var surfaceView :SurfaceView = findViewById(R.id.surfaceView)
        mSurfaceViewHolder = surfaceView.holder
        mSurfaceViewHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initCameraAndPreview()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraDevice.close()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }


        })
        btn_convert.setOnClickListener { switchCamera() }
    }

    //카메라 전환
    private fun switchCamera() {
        when (mCameraId) {
            CAMERA_BACK -> {
                mCameraId = CAMERA_FRONT
                mCameraDevice.close()
                openCamera()
            }
            else -> {
                mCameraId = CAMERA_BACK
                mCameraDevice.close()
                openCamera()
            }
        }
    }

    fun initCameraAndPreview() {
        val handlerThread = HandlerThread("CAMERA2")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        openCamera()
    }

    private fun openCamera() {
        try {
            val mCameraManager = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = mCameraManager.getCameraCharacteristics(mCameraId)
            //카메가에서 지원하는 크기 목록(이 값을 이용하여 사진 촬영 시 사진 크기를 지정할 수 있다.)
            //https://kkangsnote.tistory.com/48
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val largestPreviewSize = map!!.getOutputSizes(ImageFormat.JPEG)[0]
            setAspectRatioTextureView(largestPreviewSize.height, largestPreviewSize.width)

            mImageReader = ImageReader.newInstance(
                largestPreviewSize.width,
                largestPreviewSize.height,
                ImageFormat.JPEG,
                7
            )
            //해당권한의 기능사용 시 checkSelfPermission을 사용하여 사용자가 권한을 승인해야만 API의 사용이 사용
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            )
                return

            mCameraManager.openCamera(mCameraId, deviceStateCallback, mHandler)
        } catch (e: CameraAccessException) {
            toast("카메라를 열지 못했습니다.")
        }
    }

    private val deviceStateCallback = object : CameraDevice.StateCallback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            try {
                takePreview()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            mCameraDevice.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
//            Toast.makeText(this,"카메라를 열지 못했습니다.",Toast.LENGTH_SHORT).show()
            toast("카메라를 열지 못했습니다.")
        }
    }

    //카메라 나타내기
    @Throws(CameraAccessException::class)
    fun takePreview() {
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        mPreviewBuilder.addTarget(mSurfaceViewHolder.surface)
        mCameraDevice.createCaptureSession(
            listOf(mSurfaceViewHolder.surface, mImageReader.surface),
            mSessionPreviewStateCallback,
            mHandler
        )
    }

    //화면에 꺼지지 않도로고 설정
    private val mSessionPreviewStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            mSession = session
            try {
                // Key-Value 구조로 설정
                // 오토포커싱이 계속 동작
                mPreviewBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                //필요할 경우 플래시가 자동으로 켜짐
                mPreviewBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                )
                mSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }

        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Toast.makeText(this@ShareActivity, "카메라 구성 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        mSensorManager.registerListener(
            deviceOrientation.eventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI
        )
        mSensorManager.registerListener(
            deviceOrientation.eventListener, mMagnetometer, SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(deviceOrientation.eventListener)
    }

    //카메라에서 지원하는 크기 목록
    private fun setAspectRatioTextureView(ResolutionWidth: Int, ResolutionHeight: Int) {
        if (ResolutionWidth > ResolutionHeight) {
            val newWidth = mWidth
            val newHeight = mWidth * ResolutionWidth / ResolutionHeight
            updateTextureViewSize(newWidth, newHeight)

        } else {
            val newWidth = mWidth
            val newHeight = mWidth * ResolutionHeight / ResolutionWidth
            updateTextureViewSize(newWidth, newHeight)
        }
    }

    private fun updateTextureViewSize(viewWidth: Int, viewHeight: Int) {
//        var surfaceView :SurfaceView = findViewById(R.id.surfaceView)
        Log.d("ViewSize", "TextureView Width : $viewWidth TextureView Height : $viewHeight")
        surfaceView.layoutParams = FrameLayout.LayoutParams(viewWidth, viewHeight)
    }
}