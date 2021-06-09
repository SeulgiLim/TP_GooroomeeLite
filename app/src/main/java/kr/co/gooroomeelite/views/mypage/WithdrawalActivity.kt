package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityWithdrawalBinding

class WithdrawalActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWithdrawalBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        with(supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(R.drawable.ic_back_icon)
            setTitle(R.string.withdrawal)
        }
        binding.withdrawalNickname.setOnClickListener {

            Log.e("TEST","${binding.checkBox.isChecked}")
            Log.e("TEST","${binding.checkBox2.isChecked}")
            Log.e("TEST","${binding.checkBox3.isChecked}")

        }
        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked -> checkall() }
        binding.checkBox2.setOnCheckedChangeListener { buttonView, isChecked -> checkall() }
        binding.checkBox3.setOnCheckedChangeListener { buttonView, isChecked -> checkall() }

    }
    private fun checkall (){
        if (binding.checkBox.isChecked and binding.checkBox2.isChecked and binding.checkBox3.isChecked ){
            binding.checkbefore.setBackgroundColor(resources.getColor(R.color.skyBlue))
            binding.checkbefore.setTextColor(resources.getColor(R.color.white))
            binding.checkbefore.isClickable=true
        }
        else{
            binding.checkbefore.setBackgroundColor(resources.getColor(R.color.divide2))
            binding.checkbefore.setTextColor(resources.getColor(R.color.black))
            binding.checkbefore.isClickable=false
        }
    }

}