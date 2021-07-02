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
import kotlinx.android.synthetic.main.fragment_dialog_music.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityPrivacyPolicyBinding
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.views.login.LoginActivity

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPrivacyPolicyBinding
    lateinit var mediaplayer1 : MediaPlayer
    lateinit var mediaplayer2 : MediaPlayer
    lateinit var mediaplayer3 : MediaPlayer
    lateinit var mediaplayer4 : MediaPlayer
    lateinit var mediaplayer5 : MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBack.setOnClickListener {
            onBackPressed()
        }



        binding.toolbar.setOnClickListener {
            val mMusicView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_music, null)
            val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(mMusicView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
                window?.setGravity(Gravity.BOTTOM)
            }
            val musicstart1 = mMusicView.findViewById<RadioButton>(R.id.iv_music1)
            val musicstart2 = mMusicView.findViewById<RadioButton>(R.id.iv_music2)
            val musicstart3 = mMusicView.findViewById<RadioButton>(R.id.iv_music3)
            val musicstart4 = mMusicView.findViewById<RadioButton>(R.id.iv_music4)
            val musicstart5 = mMusicView.findViewById<RadioButton>(R.id.iv_music5)
            val radiogroup = mMusicView.findViewById<RadioGroup>(R.id.radiogroup)



            radiogroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId){
                    R.id.iv_music1 ->
                        if (musicstart1.isChecked)
                        {
                            mediaplayer1 = MediaPlayer.create(this,R.raw.firewood)
                            mediaplayer1.isLooping= true
                            mediaplayer1.start()
                        }else{
                            mediaplayer1.stop()
                        }
                    R.id.iv_music2 ->
                        if (musicstart2.isChecked)
                        {
                            mediaplayer2 = MediaPlayer.create(this,R.raw.cafe)
                            mediaplayer2.isLooping= true
                            mediaplayer2.start()
                        }else{
                            mediaplayer2.stop()
                        }
                    R.id.iv_music3 ->
                        if (musicstart3.isChecked)
                        {
                            mediaplayer3 = MediaPlayer.create(this,R.raw.library)
                            mediaplayer3.isLooping= true
                            mediaplayer3.start()
                        }else{
                            mediaplayer3.stop()
                        }
                    R.id.iv_music4 ->
                        if (musicstart4.isChecked)
                        {
                            mediaplayer4 = MediaPlayer.create(this,R.raw.wave)
                            mediaplayer4.isLooping= true
                            mediaplayer4.start()
                        }else{
                            mediaplayer4.stop()
                        }
                    R.id.iv_music5 ->
                        if (musicstart5.isChecked)
                        {
                            mediaplayer5 = MediaPlayer.create(this,R.raw.raindrop)
                            mediaplayer5.isLooping= true
                            mediaplayer5.start()
                        }else{
                            mediaplayer5.stop()
                        }
                }
            }
//            musicstart1.setOnClickListener {
//                Log.e("TEST","!")
//                if (!click1){
//                    click1== true
//                    musicstart1.setImageResource(R.drawable.ic_stopmusic)
//                    mediaplayer = MediaPlayer.create(this,R.raw.firewood)
//                    mediaplayer.isLooping= true
//                    mediaplayer.start()
//                }else{
//                    !click1 == false
//                    musicstart1.setImageResource(R.drawable.ic_playmusic)
//                    mediaplayer.stop()
//                }
//            }
//            musicstart2.setOnClickListener {
//                if (!click2){
//                    click2
//                    musicstart2.setImageResource(R.drawable.ic_stopmusic)
//                    mediaplayer = MediaPlayer.create(this,R.raw.cafe)
//                    mediaplayer.isLooping= true
//                    mediaplayer.start()
//                }else{
//                    !click2
//                    musicstart2.setImageResource(R.drawable.ic_playmusic)
//                    mediaplayer.stop()
//                }
//            }
//            musicstart3.setOnClickListener {
//                if (!click3){
//                    click3
//                    musicstart3.setImageResource(R.drawable.ic_stopmusic)
//                    mediaplayer = MediaPlayer.create(this,R.raw.library)
//                    mediaplayer.isLooping= true
//                    mediaplayer.start()
//                }else{
//                    !click3
//                    musicstart3.setImageResource(R.drawable.ic_playmusic)
//                    mediaplayer.stop()
//                }
//            }
//            musicstart4.setOnClickListener {
//                if (!click4){
//                    click4
//                    musicstart4.setImageResource(R.drawable.ic_stopmusic)
//                    mediaplayer = MediaPlayer.create(this,R.raw.wave)
//                    mediaplayer.isLooping= true
//                    mediaplayer.start()
//                }else{
//                    !click4
//                    musicstart4.setImageResource(R.drawable.ic_playmusic)
//                    mediaplayer.stop()
//                }
//            }
//            musicstart5.setOnClickListener {
//                if (!click5){
//                    click5
//                    musicstart5.setImageResource(R.drawable.ic_stopmusic)
//                    mediaplayer = MediaPlayer.create(this,R.raw.raindrop)
//                    mediaplayer.isLooping= true
//                    mediaplayer.start()
//                }else{
//                    !click5
//                    musicstart5.setImageResource(R.drawable.ic_playmusic)
//                    mediaplayer.stop()
//                }
//            }
        }
    }
    private fun check(){
        val position : Int = -1
    }
}