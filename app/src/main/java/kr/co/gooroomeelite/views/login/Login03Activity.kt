package kr.co.gooroomeelite.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityLogin03Binding
import kr.co.gooroomeelite.views.common.MainActivity

class Login03Activity : AppCompatActivity() {
    private lateinit var binding:ActivityLogin03Binding
    var auth: FirebaseAuth? = null
    var email: String? = null
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin03Binding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        setContentView(binding.root)
        email = intent.getStringExtra("email")

        //백버튼 활성화
        binding.icBack.setOnClickListener {
            onBackPressed()
        }

        binding.editTextPassword.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> changecolor()
                false -> changecolor()
            }
        }

        changecolor()

        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.editTextPassword.setBackgroundResource(R.drawable.btn_skyblue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.btnLoginNext.setOnClickListener {
            signinEmail()
        }
    }

    private fun changecolor() {
        binding.editTextPassword.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> v.setBackgroundResource(R.drawable.btn_skyblue)

                false -> v.setBackgroundResource(R.drawable.btn_white)
            }
        }
    }fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email.toString(),
            binding.editTextPassword.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.tvError.text = ""
                binding.editTextPassword.setBackgroundResource(R.drawable.btn_white)
                moveMainPage(task.result?.user)
            } else {
                binding.tvError.text = "비밀번호가 일치하지 않습니다."
                binding.editTextPassword.setBackgroundResource(R.drawable.btn_red)
                Toast.makeText(this,"TEST!", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun moveMainPage(user: FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

}