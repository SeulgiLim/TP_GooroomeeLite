package kr.co.gooroomeelite.views.login
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-21
 * @desc
 */
import android.content.Intent
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_loginemail.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityLoginemailBinding

class LoginEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginemailBinding
    var auth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    var imm : InputMethodManager? = null
    private var firestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginemailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Firebase Storage
        storage = FirebaseStorage.getInstance()

        val check = firestore!!.collection("users")
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
                binding.tvError.text = "올바른 이메일 주소를 입력해 주세요."
                binding.editTextTextEmailAddress.setBackgroundResource(R.drawable.btn_red)
            } else {
                binding.tvError.text = ""
                binding.editTextTextEmailAddress.setBackgroundResource(R.drawable.btn_white)
                check.whereEqualTo("userId", email).get().addOnSuccessListener {

                    //신규유저
                    if (it.isEmpty) {
                        val intent = Intent(this, LoginFirstActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    }
                    //이미 있는 이메일일경우
                    else {
                        val intent1 = Intent(this, LoginSecondActivity::class.java)
                        intent1.putExtra("email", email)
                        startActivity(intent1)
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        keyboard()

    }

    private fun changecolor() {
        binding.editTextTextEmailAddress.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> v.setBackgroundResource(R.drawable.btn_skyblue)

                false -> v.setBackgroundResource(R.drawable.btn_white)
            }
        }
    }

    fun keyboard() {
        with(binding) {
            editTextTextEmailAddress.apply {
                setOnEditorActionListener { v, actionId, event ->
                    val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(binding.editTextTextEmailAddress.windowToken,0)
                }
            }
        }
    }
    fun hideKeyboard(v:View){
        if (v!=null){
            imm?.hideSoftInputFromWindow(v.windowToken,0)
        }
    }
}