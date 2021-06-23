package kr.co.gooroomeelite.views.common

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kr.co.gooroomeelite.databinding.ActivitySplashBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin
import kr.co.gooroomeelite.utils.RC_SIGN_IN
import kr.co.gooroomeelite.views.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Glide.with(this).asGif().load(R.raw.).into(binding.splashImg)

        handler.postDelayed({
            startProcess()
        }, 1000L)
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            if (resultCode == RESULT_OK) {
//                val intent = Intent(this, MainActivity::class.java)
//                intent.putExtras(data!!)
//                startActivity(intent)
//                finish()
//            } else {
//                AlertDialog.Builder(this)
//                    .setMessage("로그인하지 않으면 어플을 사용할 수 없습니다.\n종료하시겠습니까?")
//                    .setPositiveButton("예") { _, _ ->
//                        finish()
//                    }
//                    .setNegativeButton("아니오") { _, _ ->
//                        signIn()
//                    }.show()
//            }
//        }
//    }
}