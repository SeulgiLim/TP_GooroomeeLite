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
import kr.co.gooroomeelite.databinding.ActivityLoginfirstBinding

class LoginFirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginfirstBinding
    var auth: FirebaseAuth? = null
    var email: String? = null
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginfirstBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        setContentView(binding.root)
        email = intent.getStringExtra("email")

        //백버튼 활성화
        binding.icBack.setOnClickListener {
            onBackPressed()
        }


        binding.editTextNewPassword.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> changecolor()
                false -> changecolor()
            }
        }
        binding.editTextNewPassword2.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> changecolor()
                false -> changecolor()
            }
        }

        changecolor()

        binding.editTextNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.editTextNewPassword.setBackgroundResource(R.drawable.btn_skyblue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editTextNewPassword2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.editTextNewPassword2.setBackgroundResource(R.drawable.btn_skyblue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnLoginNext.setOnClickListener {

            if (binding.editTextNewPassword.text.toString() != binding.editTextNewPassword2.text.toString()) {
                binding.tvError.text = "비밀번호가 일치하지 않습니다."
                binding.editTextNewPassword2.setBackgroundResource(R.drawable.btn_red)
            }
            else if (binding.editTextNewPassword.text.toString().isEmpty() or binding.editTextNewPassword2.text.toString().isEmpty()){
                binding.tvError.text = "비밀번호를 입력해주세요."
                binding.editTextNewPassword.setBackgroundResource(R.drawable.btn_red)
            }
            else {
                if(binding.editTextNewPassword.text.toString().length <8){
                    binding.tvError.text = "비밀번호는 최소 8자리 이상입니다."
                    binding.editTextNewPassword.setBackgroundResource(R.drawable.btn_red)
                }
                else{
                    binding.tvError.text = ""
                    binding.editTextNewPassword2.setBackgroundResource(R.drawable.btn_white)
                    val intent = Intent(this, LoginNicknameActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("email",email)
                    bundle.putString("password",binding.editTextNewPassword2.text.toString())
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
            }
        }
    }

    private fun changecolor() {
        binding.editTextNewPassword.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> v.setBackgroundResource(R.drawable.btn_skyblue)

                false -> v.setBackgroundResource(R.drawable.btn_white)
            }
        }

        binding.editTextNewPassword2.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> v.setBackgroundResource(R.drawable.btn_skyblue)

                false -> v.setBackgroundResource(R.drawable.btn_white)
            }
        }
    }
}