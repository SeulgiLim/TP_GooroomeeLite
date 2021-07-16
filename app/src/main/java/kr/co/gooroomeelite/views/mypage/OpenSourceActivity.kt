package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.OpenSourceAdapter
import kr.co.gooroomeelite.adapter.ServiceAdapter
import kr.co.gooroomeelite.databinding.ActivityOpenSourceBinding

class OpenSourceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOpenSourceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenSourceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        with(binding){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(this@OpenSourceActivity,
                    LinearLayoutManager.VERTICAL,false)
                adapter = OpenSourceAdapter(openSourceData())
            }
        }

    }

    private fun openSourceData(): MutableList<OpenSourceItem> {
        val openSourceList = mutableListOf<OpenSourceItem>()
        return openSourceList.apply {
            add(OpenSourceItem(getText(R.string.opensource_first).toString(), getText(R.string.opensource_firstcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_second).toString(), getText(R.string.opensource_secondcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_third).toString(), getText(R.string.opensource_thirdcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_fourth).toString(), getText(R.string.opensource_fourth).toString()))
            add(OpenSourceItem(getText(R.string.opensource_fifth).toString(), getText(R.string.opensource_fifthcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_sixth).toString(), getText(R.string.opensource_sixthcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_seventh).toString(), getText(R.string.opensource_seventhcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_eighth).toString(), getText(R.string.opensource_eighthcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_nineth).toString(), getText(R.string.opensource_ninethcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_tenth).toString(), getText(R.string.opensource_tenthcontent).toString()))
            add(OpenSourceItem(getText(R.string.opensource_eleventh).toString(), getText(R.string.opensource_eleventhcontent).toString()))
        }
    }
}