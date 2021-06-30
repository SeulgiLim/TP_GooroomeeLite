package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-29
 * @desc
 */

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityTestBinding


class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    var check1 :String? = null
    lateinit var mediaplayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTestBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        intent = Intent(this, MusicService::class.java)
        binding.btnStart.setOnClickListener {
            check1 = intent.getStringExtra("key")
            mediaplayer = MediaPlayer.create(this,R.raw.serenity)
            mediaplayer.start()
            Log.e("TEST","1")
        }
        binding.btnStop.setOnClickListener {
//            stopService(intent)
            mediaplayer.stop()
            Log.e("TEST","2")
        }
    }
}

