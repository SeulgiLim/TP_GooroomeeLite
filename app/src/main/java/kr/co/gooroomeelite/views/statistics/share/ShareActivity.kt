package kr.co.gooroomeelite.views.statistics.share

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import kr.co.gooroomeelite.R

class ShareActivity : AppCompatActivity() {
    private lateinit var mSurfaceViewHolder: SurfaceHolder

    private lateinit var mAccelerometer: Sensor //가속도계
    private lateinit var mMagnetometer: Sensor  //자력계
    private lateinit var mSensorManager: SensorManager //센서관리자

    //스마트폰 크기 호환성
    private var mHeight: Int = 0
    private var mWidth:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //상태바 숨기기?? 있어도 되고 없어도 된다.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //화면 켜짐 유지?? 아직까지 왜 있는지 모르겠음....
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        )
        setContentView(R.layout.activity_share)

        initSensor()
        initView()
    }

    private fun initSensor(){
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) //가속도계
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) //자력계

    }

    private fun initView(){
        //스마트폰 크기 호환성 확인!! 없어도 되는 코드
        with(DisplayMetrics()){
            windowManager.defaultDisplay.getMetrics(this)
            mHeight = heightPixels
            mWidth = widthPixels
        }

        //viewBinding import가 안될 때
        val surfaceView :SurfaceView = findViewById(R.id.surfaceView)
        mSurfaceViewHolder = surfaceView.holder
        mSurfaceViewHolder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                TODO("Not yet implemented")
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                TODO("Not yet implemented")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                TODO("Not yet implemented")
            }

        })
    }
}