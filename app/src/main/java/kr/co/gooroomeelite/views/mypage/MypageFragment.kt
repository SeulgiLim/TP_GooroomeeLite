package kr.co.gooroomeelite.views.mypage

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-08
 * @desc
 */

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kr.co.gooroomeelite.BuildConfig
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.FragmentMypageBinding
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin
import java.io.File

class MypageFragment(val owner:AppCompatActivity) : Fragment() {

    private lateinit var binding : FragmentMypageBinding

    var storage : FirebaseStorage? = null
    var auth : FirebaseAuth? = null
    var storageRef : StorageReference? = null
    val version = BuildConfig.VERSION_NAME
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var photoUri : String?= null
    var nickname : String?=null
    var email : String?=null

    companion object {
        fun newInstance(owner: AppCompatActivity) : Fragment {
            return MypageFragment(owner)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        uid = arguments?.getString("destinationUid")
        binding = FragmentMypageBinding.inflate(inflater,container,false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mypage,container,false)
        binding.my = this
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        email = auth!!.currentUser?.email
        owner.setSupportActionBar(binding.toolbar2)

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        getImageNickName()
        setting()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(owner.supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            setTitle("Gooroomee")
        }

        //로그아웃하기
        binding.btnLogout.setOnClickListener {
            val mLogoutView =
                LayoutInflater.from(owner).inflate(R.layout.fragment_dialog_logout, null)
            val mBuilder = androidx.appcompat.app.AlertDialog.Builder(owner).setView(mLogoutView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            val okButton = mLogoutView.findViewById<Button>(R.id.btn_logout_ok)
            val cancelButton = mLogoutView.findViewById<Button>(R.id.btn_logout_no)


            okButton.setOnClickListener {
                //로그아웃

                if(isLogin()) {
                    FirebaseAuth.getInstance().signOut()
                }

                //파이어베이스 로그아웃//


                Toast.makeText(owner,"로그아웃되었습니다.",Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(owner, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }


        //최신버전 비교하기


        //화면이동
        binding.btnProfileAccount.setOnClickListener {
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            val intent01 = Intent(owner,ProfileAccountActivity::class.java)
            intent01.putExtra("destinationUid",uid)
            startActivity(intent01)
        }

        binding.btnTermsOfService.setOnClickListener {
            val intent02 = Intent(owner,TermsOfServiceActivity::class.java)
            startActivity(intent02)
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            val intent03 = Intent(owner,PrivacyPolicyActivity::class.java)
            startActivity(intent03)
        }

        binding.btnOpenSource.setOnClickListener {
            val intent04 = Intent(owner,OpenSourceActivity::class.java)
            startActivity(intent04)
        }

        binding.btnWithdrawal.setOnClickListener {
            val intent05 = Intent(owner,WithdrawalActivity::class.java)
            startActivity(intent05)
        }
    }

    private fun getImageNickName(){
        var file : File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img")
        if(file?.isDirectory == null){
            file?.mkdir()
        }
        else
        downloadImgNickName()
    }
    private fun downloadImgNickName(){
        val num = uid
        var filename = "profile$num.jpg"
        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference
        storageRef!!.child("profile_img/$filename").child(filename).downloadUrl.addOnSuccessListener{
            Glide.with(owner).load(it).into(binding.imageView)
        }

    }
    private fun setting(){
        firestore!!.collection("users").document(getUid()!!).get().addOnSuccessListener { ds ->
            if (ds!=null){
                val contentDTO = ds.toObject(ContentDTO::class.java)
                val nickname = contentDTO!!.nickname
                val email = contentDTO!!.userId
                binding.emailaddress.text=email
                binding.nickname.text=nickname
            }
        }
    }
}

