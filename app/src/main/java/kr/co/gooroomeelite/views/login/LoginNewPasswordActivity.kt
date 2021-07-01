package kr.co.gooroomeelite.views.login
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-21
 * @desc
 */
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityLoginNewPasswordBinding

class LoginNewPasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginNewPasswordBinding
    var email: String? = null
    var imm : InputMethodManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = intent.getStringExtra("email")


        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        //백버튼 활성화
        binding.icBack.setOnClickListener {
            startActivity(Intent(this,LoginSecondActivity::class.java))
        }

        binding.btnLoginNext.setOnClickListener {
            if (email == binding.editTextFindpassword.text.toString()){
                findPassword()
            }
            else{
                binding.tvError.text = "이메일이 일치하지 않습니다."
                binding.editTextFindpassword.setBackgroundResource(R.drawable.btn_red)
            }
        }

        binding.editTextFindpassword.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> changecolor()
                false -> changecolor()
            }
        }
        changecolor()
        binding.editTextFindpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.editTextFindpassword.setBackgroundResource(R.drawable.btn_skyblue)
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    fun findPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.editTextFindpassword.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this, "비밀번호 변경 메일을 전송했습니다", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changecolor() {
        binding.editTextFindpassword.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> v.setBackgroundResource(R.drawable.btn_skyblue)

                false -> v.setBackgroundResource(R.drawable.btn_white)
            }
        }
    }
    fun hideKeyboard(v: View){
        if (v!=null){
            imm?.hideSoftInputFromWindow(v.windowToken,0)
        }
    }
}