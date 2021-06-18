package kr.co.gooroomeelite.views.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.databinding.ActivityLoginBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var email : EditText
    private lateinit var password : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.emailInput.editText!!
        password = binding.passwordInput.editText!!

        binding.loginBtn.setOnClickListener {
            emailLogin()
        }
    }

    private fun createAndLoginEmail() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString() ,password.text.toString())
            .addOnCompleteListener {
                binding.progressBar.visibility = View.GONE
                if(it.isSuccessful) {
                    Toast.makeText(this,"회원 가입 성공",Toast.LENGTH_SHORT).show()
                    moveMainPage()
                } else if (it.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                } else {
                    signinEmail()
                }
        }
    }

    private fun moveMainPage() {
        if(isLogin()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun emailLogin() {
        if(email.text.toString().isNullOrEmpty() || password.text.toString().isNullOrEmpty()) {
            Toast.makeText(this,"아이디와 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show()
        } else {
            binding.progressBar.visibility = View.VISIBLE
            createAndLoginEmail()
        }
    }

    private fun signinEmail() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                binding.progressBar.visibility = View.GONE
                if(it.isSuccessful) {
                    moveMainPage()
                } else {
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage()
    }
}