package kr.co.gooroomeelite.views.common

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.views.home.HomeFragment
import kr.co.gooroomeelite.views.login.LoginActivity
import kr.co.gooroomeelite.views.mypage.MypageFragment
import kr.co.gooroomeelite.views.statistics.StatisticsFragment
@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {
    var mBackWait : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val statisticsFragment = StatisticsFragment()
        val mypageFragment = MypageFragment(this)

        replaceFragment(homeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home -> replaceFragment(homeFragment)
                R.id.statistics-> replaceFragment(statisticsFragment)
                R.id.mypage-> replaceFragment(mypageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_LONG).show()
        } else {
            finish()
            startActivity(Intent(this,LoginActivity::class.java))
        //액티비티 종료
        }
    }
}