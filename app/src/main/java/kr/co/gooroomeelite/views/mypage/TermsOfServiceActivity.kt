package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityTermsOfServiceBinding

class TermsOfServiceActivity : AppCompatActivity() {
    private val lottieFragment by lazy{LottieTest()}
    private lateinit var binding : ActivityTermsOfServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsOfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        with(supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(R.drawable.ic_back_icon)
            setTitle(R.string.service2)
        }
        showProgressDialog()
        val handler : Handler = Handler()
        handler.postDelayed({
            hideProgressDialog()
        },5000)
    }
    private fun showProgressDialog(){
        if (!lottieFragment.isAdded){
            lottieFragment.show(supportFragmentManager,"loader")
        }
    }
    private fun hideProgressDialog(){
        if(lottieFragment.isAdded){
            lottieFragment.dismissAllowingStateLoss()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}