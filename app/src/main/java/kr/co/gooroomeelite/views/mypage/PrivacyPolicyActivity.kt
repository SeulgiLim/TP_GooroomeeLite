package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_dialog_music.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.ServiceAdapter
import kr.co.gooroomeelite.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBack.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment(this@PrivacyPolicyActivity)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        with(binding){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(this@PrivacyPolicyActivity,
                    LinearLayoutManager.VERTICAL,false)
                adapter = ServiceAdapter(privacyData())
            }
        }

    }

    private fun privacyData(): MutableList<ServiceItem> {
        val serviceList = mutableListOf<ServiceItem>()
        return serviceList.apply {
            add(ServiceItem(getText(R.string.service_first).toString(), getText(R.string.privacy_firstcontent).toString()))
            add(ServiceItem(getText(R.string.service_second).toString(), getText(R.string.privacy_secondcontent).toString()))
            add(ServiceItem(getText(R.string.service_third).toString(), getText(R.string.privacy_thirdcontent).toString()))
            add(ServiceItem(getText(R.string.service_fourth).toString(), getText(R.string.privacy_fourthcontent).toString()))
        }
    }

}