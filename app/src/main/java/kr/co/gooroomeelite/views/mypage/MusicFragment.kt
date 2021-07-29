package kr.co.gooroomeelite.views.mypage

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-29
 * @desc
 */

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.MusicAdapter
import kr.co.gooroomeelite.databinding.ActivityMusicBinding
import kr.co.gooroomeelite.databinding.FragmentMusicBinding
import kr.co.gooroomeelite.views.home.StudyEndActivity

class MusicFragment(val owner : AppCompatActivity) : Fragment() {

    private lateinit var callback: OnBackPressedCallback
    private lateinit var binding : FragmentMusicBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                owner.supportFragmentManager.beginTransaction().remove(this@MusicFragment).commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(owner, callback)

        binding = FragmentMusicBinding.inflate(inflater,container,false)
        with(binding){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(owner,
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,false)
                adapter = MusicAdapter(owner,musicData())
            }
        }
        binding.textView6.setOnClickListener{
            startActivity(Intent(owner, StudyEndActivity::class.java))
        }
        binding.icBack.setOnClickListener {
            owner.supportFragmentManager.beginTransaction().remove(this@MusicFragment).commit()
        }
        return binding.root
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

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}