package kr.co.gooroomeelite.views.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.databinding.ActivityLoginNewPasswordBinding

class LoginNewPasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginNewPasswordBinding
    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth
        binding = ActivityLoginNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //백버튼 활성화
        binding.icBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnLoginNext.setOnClickListener {
            findPassword()
        }

    }
    fun findPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(et_pw_find.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this, "비밀번호 변경 메일을 전송했습니다", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}