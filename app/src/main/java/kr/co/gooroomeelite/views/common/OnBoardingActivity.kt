package kr.co.gooroomeelite.views.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.ViewPagerAdapter
import kr.co.gooroomeelite.databinding.ActivityOnBoardingBinding
import kr.co.gooroomeelite.views.mypage.OnBoardingItem

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            viewpager2.apply {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                adapter = ViewPagerAdapter(this@OnBoardingActivity,onBoardingData())
            }
        }

    }

    private fun onBoardingData(): MutableList<OnBoardingItem> {
        val onBoardingList = mutableListOf<OnBoardingItem>()
        return onBoardingList.apply {
            add(OnBoardingItem("공부 기록\n구루미로 더 편리하게",R.drawable.background_indicator1,R.drawable.ic_indicator1))
            add(OnBoardingItem("한눈에 보는\n내 공부 그래프",R.drawable.background_indicator2,R.drawable.ic_indicator2))
            add(OnBoardingItem("오늘의 공부 시간\nSNS로 공유하세요.",R.drawable.background_indicator3,R.drawable.ic_indicator3))
        }
    }
}