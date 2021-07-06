package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_music.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityPrivacyPolicyBinding
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.views.login.LoginActivity

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBack.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment(this@PrivacyPolicyActivity)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

}




//        binding.toolbar.setOnClickListener {
//            val mMusicView =
//                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_music, null)
//            val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(mMusicView)
//            val mAlertDialog = mBuilder.show().apply {
//                window?.setBackgroundDrawable(null)
//                window?.setGravity(Gravity.BOTTOM)
//            }
//            val musicstart1 = mMusicView.findViewById<RadioButton>(R.id.iv_music1)
//            val musicstart2 = mMusicView.findViewById<RadioButton>(R.id.iv_music2)
//            val musicstart3 = mMusicView.findViewById<RadioButton>(R.id.iv_music3)
//            val musicstart4 = mMusicView.findViewById<RadioButton>(R.id.iv_music4)
//            val musicstart5 = mMusicView.findViewById<RadioButton>(R.id.iv_music5)
//
//            val mediaplayer1 = MediaPlayer.create(this,R.raw.firewood)
//            val mediaplayer2 = MediaPlayer.create(this,R.raw.cafe)
//            val mediaplayer3 = MediaPlayer.create(this,R.raw.library)
//            val mediaplayer4 = MediaPlayer.create(this,R.raw.wave)
//            val mediaplayer5 = MediaPlayer.create(this,R.raw.raindrop)
//            val radiogroup = mMusicView.findViewById<RadioGroup>(R.id.radiogroup)
//
//
//
//            radiogroup.setOnCheckedChangeListener { _, checkedId ->
//                when (checkedId){
//                    R.id.iv_music1 ->
//                        if (musicstart1.isChecked)
//                        {
//                            if (mediaplayer1.isPlaying){
//                                mediaplayer1.stop()
//                                mediaplayer1.reset()
//                            }
//                            else{
//                                mediaplayer1.isLooping= true
//                                mediaplayer1.start()
//                            }
////                            mediaplayer1 = MediaPlayer.create(this,R.raw.firewood)
////                            mediaplayer1.isLooping= true
////                            mediaplayer1.start()
//                        }else{
////                            mediaplayer1.stop()
//                        }
//                    R.id.iv_music2 ->
//                        if (musicstart2.isChecked)
//                        {
//                            mediaplayer2.isLooping= true
//                            mediaplayer2.start()
//                        }else{
//                            mediaplayer2.stop()
//                        }
//                    R.id.iv_music3 ->
//                        if (musicstart3.isChecked)
//                        {
//                            mediaplayer3.isLooping= true
//                            mediaplayer3.start()
//                        }else{
//                            mediaplayer3.stop()
//                        }
//                    R.id.iv_music4 ->
//                        if (musicstart4.isChecked)
//                        {
//                            mediaplayer4.isLooping= true
//                            mediaplayer4.start()
//                        }else{
//                            mediaplayer4.stop()
//                        }
//                    R.id.iv_music5 ->
//                        if (musicstart5.isChecked)
//                        {
//                            mediaplayer5.isLooping= true
//                            mediaplayer5.start()
//                        }else{
//                            mediaplayer5.stop()
//                        }
//                }
//            }
//        }
//    }
//}