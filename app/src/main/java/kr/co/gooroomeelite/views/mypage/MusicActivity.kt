package kr.co.gooroomeelite.views.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.MusicAdapter
import kr.co.gooroomeelite.databinding.ActivityMusicBinding
import kr.co.gooroomeelite.views.home.StudyEndActivity

class MusicActivity : AppCompatActivity() {
    lateinit var binding : ActivityMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(this@MusicActivity,LinearLayoutManager.VERTICAL,false)
                adapter = MusicAdapter(this@MusicActivity,musicData())
            }
        }
        binding.textView6.setOnClickListener{
            startActivity(Intent(this,StudyEndActivity::class.java))
        }
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun musicData(): MutableList<MusicItem> {
        val musicList = mutableListOf<MusicItem>()
        return musicList.apply {
            add(MusicItem(getText(R.string.music2).toString(),R.raw.firewood))
            add(MusicItem(getText(R.string.music3).toString(),R.raw.cafe))
            add(MusicItem(getText(R.string.music4).toString(),R.raw.library))
            add(MusicItem(getText(R.string.music5).toString(),R.raw.wave))
            add(MusicItem(getText(R.string.music6).toString(),R.raw.raindrop))
        }
    }

}