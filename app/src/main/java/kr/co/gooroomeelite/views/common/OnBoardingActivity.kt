package kr.co.gooroomeelite.views.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.activity_on_boarding.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.ViewPagerAdapter
import kr.co.gooroomeelite.databinding.ActivityOnBoardingBinding
import kr.co.gooroomeelite.views.mypage.OnBoardingItem


class OnBoardingActivity : AppCompatActivity(){

    private lateinit var binding : ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.indicator)
        with(binding){
            viewpager2.apply {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                adapter = ViewPagerAdapter(this@OnBoardingActivity,onBoardingData())
            }
        }
        dotsIndicator.setViewPager2(binding.viewpager2)
    }
    private fun onBoardingData(): MutableList<OnBoardingItem> {
        val onBoardingList = mutableListOf<OnBoardingItem>()
        return onBoardingList.apply {
            add(OnBoardingItem(R.drawable.img_onboarding1,getText(R.string.onboarding_title1).toString(),getText(R.string.onboarding_content1).toString()))
            add(OnBoardingItem(R.drawable.img_onboarding2,getText(R.string.onboarding_title2).toString(),getText(R.string.onboarding_content2).toString()))
            add(OnBoardingItem(R.drawable.img_onboarding3,getText(R.string.onboarding_title3).toString(),getText(R.string.onboarding_content3).toString()))
            add(OnBoardingItem(R.drawable.img_onboarding4,getText(R.string.onboarding_title4).toString(),getText(R.string.onboarding_content4).toString()))
        }
    }
}