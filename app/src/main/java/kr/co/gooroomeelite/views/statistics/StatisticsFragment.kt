package kr.co.gooroomeelite.views.statistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kr.co.gooroomeelite.R

class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pageView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val tabLayout: TabLayout = pageView.findViewById(R.id.tab_layout)

        tabLayout.addTab(tabLayout.newTab().setText("일간"))
        tabLayout.addTab(tabLayout.newTab().setText("주간"))
        tabLayout.addTab(tabLayout.newTab().setText("월간"))

        val pagerAdapter = PagerAdapter(parentFragmentManager, 3)
        val viewPager: ViewPager = pageView.findViewById(R.id.viewPager)
        viewPager.adapter = pagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                viewPager.currentItem = tab!!.position
                viewPager.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        return pageView
    }
}

class PagerAdapter(
    fragmentManager: FragmentManager,
    val tabCount: Int
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return tabCount
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return DayFragment()
            1 -> return WeekFragment()
            2 -> return MonthFragment()
            else -> return DayFragment()
        }
    }
}