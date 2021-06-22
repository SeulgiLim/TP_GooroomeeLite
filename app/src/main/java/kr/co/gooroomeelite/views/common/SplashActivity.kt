package kr.co.gooroomeelite.views.common

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
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
//            finish()
        }, 1000L)
    }

    private fun startProcess() {
        if(isLogin()) {
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            signIn()
        }
    }

    private fun signIn() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setTheme(AuthUI.getDefaultTheme())
                .setLogo(AuthUI.NO_LOGO)
                .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build(), AuthUI.IdpConfig.EmailBuilder().build()))
//                .setTosAndPrivacyPolicyUrls("https://naver.com", "https://google.com")
//                .setIsSmartLockEnabled(true)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtras(data!!)
                startActivity(intent)
                finish()
            } else {
                signIn()
            }
        }
    }
}