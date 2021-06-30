package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-29
 * @desc
 */

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.co.gooroomeelite.databinding.ActivityTestBinding
import kr.co.gooroomeelite.utils.MusicService


class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    var check1 :String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTestBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        intent = Intent(this, MusicService::class.java)
        binding.btnStart.setOnClickListener {
            check1 = intent.getStringExtra("key")

            startService(intent)
            Log.e("TEST","1")
        }
        binding.btnStop.setOnClickListener {
            stopService(intent)
            Log.e("TEST","2")
        }
    }
}