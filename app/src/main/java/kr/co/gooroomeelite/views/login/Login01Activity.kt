package kr.co.gooroomeelite.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityLogin01Binding

class Login01Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLogin01Binding
    var auth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    private var firestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin01Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Firebase Storage
        storage = FirebaseStorage.getInstance()


        val check = firestore!!.collection("users")
        //현재 아이디로 입력한 이메일

        binding = ActivityLogin01Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBack.setOnClickListener {
            onBackPressed()
        }

        binding.editTextTextEmailAddress.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> changecolor()
                false -> changecolor()
            }
        }
        changecolor()
        binding.editTextTextEmailAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.editTextTextEmailAddress.setBackgroundResource(R.drawable.btn_skyblue)
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.btnLoginNext.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editTextTextEmailAddress.text.toString())
                    .matches()
            ) {
                binding.tvError.text = "이메일 형식을 입력해 주세요"
                binding.editTextTextEmailAddress.setBackgroundResource(R.drawable.btn_red)
            } else {
                binding.tvError.text = ""
                binding.editTextTextEmailAddress.setBackgroundResource(R.drawable.btn_white)
                check.whereEqualTo("userId", email).get().addOnSuccessListener {

                    //신규유저
                    if (it.isEmpty) {
                        val intent = Intent(this, Login02Activity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    }
                    //이미 있는 이메일일경우
                    else {
                        val intent1 = Intent(this, Login03Activity::class.java)
                        intent1.putExtra("email", email)
                        startActivity(intent1)
                    }
                }
            }

        }
    }
    private fun changecolor() {
        binding.editTextTextEmailAddress.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> v.setBackgroundResource(R.drawable.btn_skyblue)

                false -> v.setBackgroundResource(R.drawable.btn_white)
            }
        }
    }
}