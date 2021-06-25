package kr.co.gooroomeelite.views.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.co.gooroomeelite.databinding.ActivityLoginStudyTimeBinding
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.views.common.MainActivity

class LoginStudyTimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginStudyTimeBinding
    var bundle: Bundle? = null
    var email: String? = null
    var password: String? = null
    var nickname: String? = null
    var studyTime: Int = 0
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStudyTimeBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        bundle = intent.getBundleExtra("bundle")
        email = bundle?.getString("email")
        password = bundle?.getString("password")
        nickname = bundle?.getString("nickname")
        timepciker()


        setContentView(binding.root)
        //백버튼 활성화
        binding.icBack.setOnClickListener {
            startActivity(Intent(this,LoginNicknameActivity::class.java))
        }

        binding.btnLoginNext.setOnClickListener {
            timecal()
            if (password!=null){
                signinAndSignup()
            }
            else{
                signingoogle()
            }
        }
    }

    private fun signingoogle() {
        contentUpload()
        val contentDTO = ContentDTO()
        contentDTO.google = true
        val data = hashMapOf<String, Any>()
        data["google"] = contentDTO.google
        firestore?.collection("users")?.document(LoginUtils.getUid()!!)?.update(data)

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(
            email.toString(),
            password.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Creating a user account
                contentUpload()
                moveMainPage(task.result?.user)
                finish()
            }
            else if (task.exception?.message.isNullOrEmpty()) {
            }
            else {
            }
        }
    }

    private fun contentUpload() {
        val contentDTO = ContentDTO()
        contentDTO.userId = auth?.currentUser?.email
        contentDTO.nickname = nickname
        contentDTO.studyTime = studyTime
        firestore?.collection("users")?.document(LoginUtils.getUid()!!)?.set(contentDTO)
        setResult(Activity.RESULT_OK)
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun timepciker() {
        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 5//6개
        binding.minutePicker.displayedValues = arrayOf("00", "10", "20", "30", "40", "50")

    }
    fun timecal(){
        studyTime = binding.hourPicker.value * 60 + binding.minutePicker.value * 10
    }
}