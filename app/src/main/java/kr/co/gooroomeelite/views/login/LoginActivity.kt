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
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityLoginBinding
import kr.co.gooroomeelite.views.common.MainActivity
import kr.co.gooroomeelite.views.common.OnBoardingActivity
import kr.co.gooroomeelite.views.mypage.PrivacyPolicyActivity
import kr.co.gooroomeelite.views.mypage.TermsOfServiceActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 100
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Firebase Storage
        storage = FirebaseStorage.getInstance()

        binding.btnPrivacy.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }
        binding.btnService.setOnClickListener {
            startActivity(Intent(this,TermsOfServiceActivity::class.java))
        }
        binding.startEmail.setOnClickListener {
            //이메일로 시작
            startActivity(Intent(this, LoginEmailActivity::class.java))
        }
        binding.startGoogle.setOnClickListener {
            //구글로 시작
            googleLogin()
            Log.e("TEST,","1")
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        Log.e("TEST,","2")
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        Log.e("TEST,","3")
    }

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent

        Log.e("TEST,","4")
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)

        Log.e("TEST,","5")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {

            Log.e("TEST,","6")
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {

                Log.e("TEST,","7")
                var account = result.signInAccount
                //Second step

                Log.e("TEST,","8")
                firebaseAuthWithGoogle(account)

                Log.e("TEST,","9")
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        Log.e("TEST,","10")
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Log.e("TEST,","11")
                //Login

                val email = account?.email
                val check = firestore!!.collection("users")
                Log.e("TEST,","12")

                check.whereEqualTo("userId", email).get().addOnSuccessListener {
                    //신규유저
                    if (it.isEmpty) {
                        Log.e("TEST,","13")
                        val intent = Intent(this, LoginNicknameActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("email",account?.email)
                        intent.putExtra("bundle",bundle)
                        Log.e("TEST,","14")
                        startActivity(intent)
                        finish()
                    }
                    //이미 있는 이메일일경우
                    else {
                        Log.e("TEST,","15")
                        val intent1 = Intent(this, MainActivity::class.java)
                        Log.e("TEST,","16")
                        startActivity(intent1)
                    }
                }
            } else {
                //Show the error message
                Toast.makeText(this, "TEST#", Toast.LENGTH_LONG).show()
            }
        }
    }
}

