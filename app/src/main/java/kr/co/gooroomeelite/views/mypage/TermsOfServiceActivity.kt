package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_terms_of_service.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.ServiceAdapter
import kr.co.gooroomeelite.databinding.ActivityTermsOfServiceBinding

class TermsOfServiceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTermsOfServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsOfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        with(binding){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(this@TermsOfServiceActivity,LinearLayoutManager.VERTICAL,false)
                adapter = ServiceAdapter(serviceData())
            }
        }

    }

    private fun serviceData(): MutableList<ServiceItem> {
        val serviceList = mutableListOf<ServiceItem>()
        return serviceList.apply {
            add(ServiceItem(getText(R.string.service_first).toString(), getText(R.string.service_firstcontent).toString()))
            add(ServiceItem(getText(R.string.service_second).toString(), getText(R.string.service_secondcontent).toString()))
            add(ServiceItem(getText(R.string.service_third).toString(), getText(R.string.service_thirdcontent).toString()))
            add(ServiceItem(getText(R.string.service_fourth).toString(), getText(R.string.service_fourthcontent).toString()))
            add(ServiceItem(getText(R.string.service_fifth).toString(), getText(R.string.service_fifthcontent).toString()))
            add(ServiceItem(getText(R.string.service_sixth).toString(), getText(R.string.service_sixthcontent).toString()))
            add(ServiceItem(getText(R.string.service_seventh).toString(), getText(R.string.service_seventhcontent).toString()))
            add(ServiceItem(getText(R.string.service_eighth).toString(), getText(R.string.service_eighthcontent).toString()))
            add(ServiceItem(getText(R.string.service_nineth).toString(), getText(R.string.service_ninethcontent).toString()))
            add(ServiceItem(getText(R.string.service_tenth).toString(), getText(R.string.service_tenthcontent).toString()))
        }
    }
}