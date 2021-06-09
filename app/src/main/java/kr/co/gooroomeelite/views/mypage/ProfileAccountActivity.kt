package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityProfileAccountBinding

class ProfileAccountActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProfileAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        with(supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(R.drawable.ic_back_icon)
            setTitle(R.string.profile_account)
        }



    }
}