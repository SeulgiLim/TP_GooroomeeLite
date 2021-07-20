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
            add(OnBoardingItem(getText(R.string.onboarding_content1).toString(),R.drawable.background_indicator1,R.drawable.ic_indicator1))
            add(OnBoardingItem(getText(R.string.onboarding_content2).toString(),R.drawable.background_indicator2,R.drawable.ic_indicator2))
            add(OnBoardingItem(getText(R.string.onboarding_content3).toString(),R.drawable.background_indicator3,R.drawable.ic_indicator3))
        }
    }
}