package kr.co.gooroomeelite.views.common
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-14
 * @desc
 */
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
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
        binding.viewpager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewpager2.adapter = ViewPagerAdapter(this@OnBoardingActivity,onBoardingData())
        binding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicatorvisible(position)
            }
        })
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
    fun indicatorvisible (position: Int){
        when(position){
            0,1,2 -> binding.indicator.visibility = View.VISIBLE
            3 -> binding.indicator.visibility = View.GONE
        }
    }
}