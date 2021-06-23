package kr.co.gooroomeelite.views.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.views.home.HomeFragment
import kr.co.gooroomeelite.views.mypage.MypageFragment
import kr.co.gooroomeelite.views.statistics.StatisticsFragment

class MainActivity : AppCompatActivity() {
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
                R.id.mypage-> {
                    var mypageFragment = MypageFragment(this)
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    bundle.putString("destinationUid",uid)
                    mypageFragment.arguments=bundle
                    replaceFragment(mypageFragment)
                }
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
}