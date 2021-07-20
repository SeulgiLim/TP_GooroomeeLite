package kr.co.gooroomeelite.views.statistics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.gooroomeelite.R
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.Q)
class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pageView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val tabs: TabLayout = pageView.findViewById(R.id.tabs)

        val dayFragment = DayFragment()
        val weekFragment = WeekFragment()
        val monthFragment = MonthFragment()

        parentFragmentManager
            .beginTransaction()
            .add(R.id.chart_container, dayFragment)
            .commit()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position


                var selected: Fragment? = null
                if (position == 0) {
                    selected = dayFragment
                } else if (position == 1) {
                    selected = weekFragment
                } else if (position == 2) {
                    selected = monthFragment
                }
                if (selected != null) {
                    parentFragmentManager.beginTransaction().replace(R.id.chart_container, selected)
                        .commit()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        return pageView
    }

}

