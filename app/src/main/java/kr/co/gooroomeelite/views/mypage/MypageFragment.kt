package kr.co.gooroomeelite.views.mypage

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-08
 * @desc
 */

import android.content.Intent
import android.net.Uri
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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kr.co.gooroomeelite.BuildConfig
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.FragmentMypageBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin
import java.io.File

class MypageFragment(val owner:AppCompatActivity) : Fragment() {

    private lateinit var binding : FragmentMypageBinding

    var storage : FirebaseStorage? = null

    var storageRef : StorageReference? = null

    var photoUri : Uri? = null

    val version = BuildConfig.VERSION_NAME
    //Storage에서 받아오는 이미지를 프로필 사진으로.
    //Storage에서 받아오는 닉네임을 닉네임 스트링으로.

    companion object {
        fun newInstance(owner: AppCompatActivity) : Fragment {
            return MypageFragment(owner)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater,container,false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mypage,container,false)
        binding.my = this
        getImage(10)
        owner.setSupportActionBar(binding.toolbar2)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getImage(10)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(owner.supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(kr.co.gooroomeelite.R.drawable.ic_back_icon)
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
//                owner.finish()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(owner, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }


        //최신버전 비교하기


        //화면이동
        binding.btnProfileAccount.setOnClickListener {
            val intent01 = Intent(owner,ProfileAccountActivity::class.java)
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




        //건의사항 메일보내기
//        binding.btnReport.setOnClickListener {
//            val sendEmail = Intent(Intent.ACTION_SEND)
//            with(sendEmail) {
//                type = "plain/Text"
//                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
//                putExtra(
//                    Intent.EXTRA_SUBJECT,
//                    "<" + getString(R.string.app_name) + " " + getString(R.string.report) + ">"
//                )
//                putExtra(
//                    Intent.EXTRA_TEXT,
//                    "기기명 (Device):\n안드로이드 OS (Android OS):\n내용 (Content):\n"
//                )
//                type = "message/rfc822"
//            }
//            startActivity(sendEmail)
//        }
    }

    private fun getImage(num:Int){
        var file : File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img")
        if(file?.isDirectory == null){
            file?.mkdir()
        }
        else
        downloadImg(num)
    }
    private fun downloadImg(num: Int){
        var filename = "profile$num.jpg"
        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference
        storageRef!!.child("profile_img/$filename").child(filename).downloadUrl.addOnSuccessListener{
            Glide.with(owner).load(it).into(binding.imageView)
        }
            .addOnSuccessListener {

                Toast.makeText(owner,"다운로드 되었습니다.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {

                Toast.makeText(owner,"다운로드실패 되었습니다.", Toast.LENGTH_LONG).show()
            }
    }

}

