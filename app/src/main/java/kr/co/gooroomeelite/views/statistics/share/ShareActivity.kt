package kr.co.gooroomeelite.views.statistics.share

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.WindowManager
import kr.co.gooroomeelite.R

class ShareActivity : AppCompatActivity() {
    private lateinit var mSurfaceViewHolder: SurfaceHolder

    private lateinit var mAccelerometer: Sensor //가속도계
    private lateinit var mMagnetometer: Sensor  //자력계
    private lateinit var mSensorManager: SensorManager //센서관리자


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
    }

    private fun initSensor(){
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) //가속도계
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) //자력계

    }
}