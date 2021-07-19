package kr.co.gooroomeelite.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_on_boarding.view.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ItemViewpagerOnboardingBinding
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.utils.RC_SIGN_IN
import kr.co.gooroomeelite.views.common.MainActivity
import kr.co.gooroomeelite.views.login.LoginActivity
import kr.co.gooroomeelite.views.mypage.OnBoardingItem
import splitties.systemservices.activityManager
import kotlin.coroutines.coroutineContext

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-19
 * @desc
 */
class ViewPagerAdapter(private val owner : AppCompatActivity,
                       private val onBoardingList : MutableList<OnBoardingItem>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {


    inner class ViewPagerViewHolder(private val binding : ItemViewpagerOnboardingBinding) : RecyclerView.ViewHolder(binding.root){
        val tvonboarding : TextView = itemView.findViewById(R.id.tv_onboarding)
        val ivonboarding : ImageView = itemView.findViewById(R.id.iv_onboarding)
        val ivindicator : ImageView = itemView.findViewById(R.id.iv_indicator)
        val btnonboarding : Button = itemView.findViewById(R.id.btn_onboarding)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = ItemViewpagerOnboardingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val onboardingdata = onBoardingList[position]
        with(holder) {
            tvonboarding.text = onboardingdata.tvonboarding
            ivonboarding.setImageResource(onboardingdata.ivonboarding)
            ivindicator.setImageResource(onboardingdata.ivindicator)
            if (position == 2 ){
                btnonboarding.visibility = View.VISIBLE
            }
            btnonboarding.setOnClickListener {
                signIn()
            }
        }

    }
    override fun getItemCount() : Int {
        return onBoardingList.size
    }


    private fun signIn() {
        owner.startActivityForResult(Intent(owner,LoginActivity::class.java),
        RC_SIGN_IN)
        owner.finish()
    }
}
