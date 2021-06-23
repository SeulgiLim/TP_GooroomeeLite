package kr.co.gooroomeelite.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.co.gooroomeelite.databinding.ActivityLoginBinding
import kr.co.gooroomeelite.views.common.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPreview.setOnClickListener {
            val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.startEmail.setOnClickListener {
            //이메일로 시작
            startActivity(Intent(this,LoginEmailActivity::class.java))
            Toast.makeText(this,"이메일로 시작",Toast.LENGTH_SHORT).show()
        }
        binding.startGoogle.setOnClickListener {
            //구글로 시작

            Toast.makeText(this,"구글로 시작",Toast.LENGTH_SHORT).show()
        }

    }
}