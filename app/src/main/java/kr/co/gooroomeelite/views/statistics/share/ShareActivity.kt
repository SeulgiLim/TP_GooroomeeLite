package kr.co.gooroomeelite.views.statistics.share

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.*
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
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
import kr.co.gooroomeelite.views.statistics.share.extensions.loadCenterCrop
import kr.co.gooroomeelite.views.statistics.share.util.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ShareActivity : AppCompatActivity() {

    //ShareActivity에 binding객체를 받아온다.
    private lateinit var binding: ActivityShareBinding
    private lateinit var cameraExcutor :  ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }
    //카메라 기능을 사용하기 위해서!! 카메라 얻어오면 이후 실행 리스너 등록
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private lateinit var imageCapture: ImageCapture

    private var camera: Camera? = null
    private var root: View? = null

    private var isCapturing : Boolean = false

    //이미지가 촬영된 이후레 이미지를 관리한다.
    private var uriList = mutableListOf<Uri>()

    private val displayManager by lazy{
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager //디스플레이가 변경(카메라 회전을 했을 때,카메가 화면이 바뀌었을 때)이 되었을 때 그 로테이션의 값을 얻어와서 지정을 해주기 위함이다.
    }

    private var displayId : Int = 1
    private val displayListener = object: DisplayManager.DisplayListener{
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int) = Unit

        override fun onDisplayChanged(displayId: Int) {
            if(this@ShareActivity.displayId == displayId) {//디스플레이가 변경 시(화면이 회전 시) 처리 필요
                if (::imageCapture.isInitialized && root != null) {//현재 바라보고 있는 화면이 어디있는지
                    //화면 회전 시 대응해줄 수 있는 코드,  만약에 이 값이 잘못된 코드이면 INVALID_ROTATION(잘못된 회전)로 처리
                    imageCapture.targetRotation = root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION //?:(이 연산자)만약 좌항이 INVALID_ROTATION이면 INVALID_ROTATION를 반환한다.
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater) //inflate, xml에 씌어져 있는 view의 객체를 실제로 가져오는 것!!
        root = binding.root //현재 디스플레이 상태??24:37
        setContentView(binding.root) //R.id.activity_main

        startCamera(binding.viewFinder)
    }

    private fun startCamera(viewFinder : PreviewView){ //어떤 걸 넘겨줄 것인가?
        displayManager.registerDisplayListener(displayListener,null)
        cameraExcutor = Executors.newSingleThreadExecutor()
        viewFinder.postDelayed({ //카메라가 부응이 되는 시점에
            //현재 카메라가 보여지고 있는 displayId이다.
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        },10) //10초딜레이
    }

    private fun bindCameraUseCase() = with(binding){
        //화면 회전에 대해 체크
        val rotation = viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build()//카메라 후면 인식

        //카메라가 정상적으로 가져올 수 있는 상태가 되어있는지를 확인한다.
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get() //카메라 위치 가져오기
            val preview = Preview.Builder().apply{
                setTargetAspectRatio(AspectRatio.RATIO_4_3) //화면상에서 어떤 뷰를 보이게 될지 4대 3으로 지정
                setTargetRotation(rotation)
//                setTargetResolution() //이 메소드 사용 시 원하는 해상도 사용 가능, 이 메소드 미사용 시 최고 해상도로 지정된다.
            }.build()//preview를 어떤 식으로 새팅할 건지 확인한다.

            val imageCaptureBuilder = ImageCapture.Builder()//카메라의 이미지를 캡처하는 수단을 만듬
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)//지연을 최소화시킴
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)    //쵤영 시 이미지가 preview에서 보이는 4대 3비율을 똑같이 지정한다.
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO) //플래시 사용
            imageCapture = imageCaptureBuilder.build()

            try{//예외처리
                cameraProvider.unbindAll() //카메라가 기존의 바인딩 되어 있는 경우에 해제를 해줘야 한다.
                camera = cameraProvider.bindToLifecycle(
                    this@ShareActivity,cameraSelector,preview,imageCapture
                ) //카메라 객체를 가져와야 한다.
                preview.setSurfaceProvider(viewFinder.surfaceProvider) //화상으로 보여줄 수 있도록
                bindCaptureListener()//캡처기능, 외부에다가 저장된 Uri를 알려줘야 하기 때문에
                bindZoomListner()
            }catch(e:Exception){
                e.printStackTrace()
            }
        },cameraMainExecutor) //카메라 용 쓰레드가 따로 필요하다
    }

    private fun bindZoomListner() = with(binding){
        //줌아웃 시 두 손가락이 키다운?이라는 키를 받아서 두가지의 값이 얾마나 늘어나냐 줄어드냐를 값을 계산해서 콜백으로 넘겨주는 것!!
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f//배율 값 계산, 비율값 가져옴
                val delta = detector?.scaleFactor//currentZoomRatio 이 값을 가지고 얼마나 움직였는지,움직인 실제 비율값!!
                //delta(이 비율값)을 기준으로 계산을 해준다. 이 값을 곱하면 그 다음에 얼마나 확대를 할 건지
                   camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta!!)
                return true//콜백 메서드
            }
        }
        @SuppressLint("ClickableViewAccessibility")//터치 이벤트를 성공적으로 작동했다는 것을 보여주기 위해 사용
        val scaleGestureDetector = ScaleGestureDetector(this@ShareActivity,listener) //위에 listener 등
        viewFinder.setOnTouchListener{_,event-> //_(view)는 안 쓰니까 제외
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }//터치 시 해당 이벤트를 scaleGestureDetector(스케일 제스처)로부터 바인딩
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


    private var contentUri : Uri? = null
    private fun captureCamera(){
        if(::imageCapture.isInitialized.not()) return //초기화가 안 되어 있으면 return을 해서 함수를 실행하지 못하게 막는다.
        val photoFile = File( //특정 위치에 넣어줄 수 있도록 파일을 넣어준다. 즉,PathUtility라는 것을 구현해서 외장하드에 구현할 수 있도록 한다.
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(//하위에는 파일명을 넣어 줄 것이다.
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg" //이 시간대로 format이 지정이 되었음, .jpg는 확장자명
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build() //이 파일을 쓸 수 있는 아웃풋
        //imageCapture가 초기화가 된 상태에서 확인이 되었음
        imageCapture.takePicture(outputOptions,cameraExcutor,object:ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                //outputFileResults에서 꺼내서 Saved된(저장된) Uri를 꺼낸다.
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile) //(이 값이 null일 수 있다.)저장이 안되어 있는 경우에는 아까 fromFile하는 곳에다가 photoFile를 지정해준다.!!
                //가장 최근에 저장했었던 fileUri를 보개 위해서
                contentUri = savedUri //저장된 Uri를 넣어준다.
                updateSavedImageContent()

            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                isCapturing = false
            }

        })
    }

    //이미지가 저장이 되었으니 다른 갤러리를 보여줄 수 있도록 설정해 달라!!
    private fun updateSavedImageContent() {
        contentUri?.let{
            isCapturing = try{
                //파일이 어디에 씌어졌지 작성해주기 위한 함수가 있었다. ,외부 저장소에 접근 후 갤러리 어디쪽에 넣어서 쓸 것인지 지정해준다.
                //uri를 그래도 가져올 수 없으니 pathutil에거 가지고 온다., String path를 받고 file쪽에 넣어주면 된다.
                val file = File(PathUtil.getPath(this,it) ?: throw FileNotFoundException()) //만약 파일을 없는 경우에 에러를 내보낸다.
                //이거를 밖에다 어떻게 처리할 것 인지는 이미지에 image/jpeg?스택을 갖고 있는 파일을 외부로 보내서 처리해주겠다.즉, 외부에서도 이 파일을 읽힌다.null은 콜백은 따로 지정하지 않겠다는 뜻이다.
                MediaScannerConnection.scanFile(this,arrayOf(file.path),arrayOf("image/jpeg"),null)
                //사진이 저장한 상태에서 미리보기 이미지를 보여주면 좋을 것 같다 생각을 했는데...
                Handler(Looper.getMainLooper()).post{ //getMainLooper()함수는 Main Thread(UI Thread)가 사용하는 Looper 즉 언제든 Main Looper를 반환한다.
                    binding.previewImageVIew.loadCenterCrop(url = it.toString(), corner = 4f)//현재 메인쓰레드에서 이미지를 처리해줄 수 있도록 한다.
                }
                uriList.add(it)
                false
            }catch (e: Exception){
                e.printStackTrace()//에러가 있기 때문에 토스트 뛰어준다. 중간에 유실될 수 있다는 가정 하에 인 것!!
                Toast.makeText(this,"파일이 존재하지 않습니다.",Toast.LENGTH_SHORT).show()
                false //에러가 나면 false로 처리한다.
            }
        }
    }
    companion object{
        //권한 관리를 해 줄 수 있도록 한다.(난 이미 statisticsFramgment에서 permission check를 해)
        private const val REQUEST_CODE_PERMISSIONS = 10 //숫자는 아무거난 지정한다.
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        //후면 카메라를 이용(현재 우리가 쓸 수 있는 카메라가 무엇인지)
        private val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK
//        private val LENS_BACKING: INt = CameraSelector.LENS_FACING_FRONT
        //파일 이름명 포맷을 만들어 놓는다.
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}