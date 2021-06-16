package kr.co.gooroomeelite.views.statistics.share


import android.content.Context
import android.hardware.camera2.*
import android.hardware.display.DisplayManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityShareBinding
import java.lang.Exception
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.jar.Manifest


class ShareActivity : AppCompatActivity() {

    //ShareActivity에 binding객체를 받아온다.
    private lateinit var binding: ActivityShareBinding

    private lateinit var cameraExcutor :  ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }
    //카메라 기능을 사용하기 위해서!! 카메라 얻어오면 이후 실행 리스너 등록
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private lateinit var imageCapture: ImageCapture

    private var camera: Camera? = null

    private val displayManager by lazy{
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager //디스플레이가 변경(카메라 회전을 했을 때,카메가 화면이 바뀌었을 때)이 되었을 때 그 로테이션의 값을 얻어와서 지정을 해주기 위함이다.
    }

    private var displayId : Int =1
    private val displayListener = object: DisplayManager.DisplayListener{
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int) = Unit

        override fun onDisplayChanged(displayId: Int) {
            if(this@ShareActivity.displayId == displayId){

            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCamera(binding.viewFinder)
    }

    private fun startCamera(viewFinder : PreviewView){ //어떤 걸 넘겨줄 것인가?
        displayManager.registerDisplayListener(displayListener,null)
        cameraExcutor = Executors.newSingleThreadExecutor()
        viewFinder.postDelayed({ //카메라가 부응이 되는 시점에
            //현재 카메라가 보여지고 있는 displayId이다.
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        },10)
    }

    private fun bindCameraUseCase() = with(binding){
        //화면 회전에 대해 체크
        val rotation = viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build()//카메라 후면 인식

        //카메라가 정상적으로 가져올수 있는 상태가 되어있는지를 확인한다.
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get() //카메라 위치 가져오기
            val preview = Preview.Builder().apply{
                setTargetAspectRatio(AspectRatio.RATIO_4_3) //화면상에서 어떤 뷰를 보이게 될지 4대 3으로 지정
                setTargetRotation(rotation)
//                setTargetResolution() //이 메소드 사용 시 원하는 해상도 사용 가능, 이 메소드 미사용 시 최고 해상도로 지정된다.
            }.build()//preview를 어떤 식으로 새팅할 건지 확인한다.

            val imageCaptureBuilder = ImageCapture.Builder()//카메라의 이미지를 캡처하는 수단을 만듬
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)//지연을 최소화시킴
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)    //쵤영 시 preview에서 보이는 4대 3비율을 똑같이 지정한다.
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            imageCapture = imageCaptureBuilder.build()

            try{//예외처리
                cameraProvider.unbindAll() //카메라가 기존의 바인딩 되어 있는 경우에 해제를 해줘야 한다.
                camera = cameraProvider.bindToLifecycle(
                    this@ShareActivity,cameraSelector,preview,imageCapture
                ) //카메라 객체를 가져와야 한다.
                preview.setSurfaceProvider(viewFinder.surfaceProvider) //화상으로 보여줄 수 있도록
            }catch(e:Exception){
                e.printStackTrace()
            }
        },cameraMainExecutor) //카메라 용 쓰레드가 따로 필요하다
    }

    companion object{
        //권한 관리를 해 줄 수 있도록 한다.(난 이미 statisticsFramgment에서 permission check를 해)
        private const val REQUEST_CODE_PERMISSIONS = 10 //숫자는 아무거난 지정한다.
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        //현재 우리가 쓸 수 있는 카메라가 무엇인지(후면 카메라를 이용하겠다!!)
        private val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK
    }
}