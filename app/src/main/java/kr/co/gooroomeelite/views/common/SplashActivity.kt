package kr.co.gooroomeelite.views.common
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-30
 * @desc
 */
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.co.gooroomeelite.databinding.ActivitySplashBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin
import kr.co.gooroomeelite.utils.MyJobService
import kr.co.gooroomeelite.utils.RC_SIGN_IN
import kr.co.gooroomeelite.views.login.LoginActivity
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler.postDelayed({
            checkFirstRun()
                            }, 1000L)
        job()
    }

    private fun startProcess() {
        if(isLogin()) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            signIn()
        }
    }

    private fun signIn() {
        startActivityForResult(
            Intent(this, LoginActivity::class.java),
            RC_SIGN_IN
        )
        finish()
    }

    private fun job(){
        val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceComponent = ComponentName(this,MyJobService::class.java)
        val jobInfo = JobInfo.Builder(1,serviceComponent)
            .setPeriodic(TimeUnit.MINUTES.toMillis(15))
            .build()
        js.schedule(jobInfo)
        Log.e("TAG","Schedulded JobA")
    }

    private fun moveToOnBoarding(){
        finish()
        startActivity(Intent(this,OnBoardingActivity::class.java))
    }

    private fun checkFirstRun(){
        val prefs = getSharedPreferences("Prefs", MODE_PRIVATE)
        val isfirst :Boolean = prefs.getBoolean("isFirstRun",true)
        if (isfirst){
            prefs.edit().putBoolean("isFirstRun",false).apply()
            moveToOnBoarding()
        }
        else{
            startProcess()
        }
    }
}