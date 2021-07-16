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

    private lateinit var horizontalChart: HorizontalBarChart
    private lateinit var horizontal2Chart: HorizontalBarChart

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

//        val shareButton: Button = pageView.findViewById(R.id.share_button)
//        shareButton.setOnClickListener{
//            requestPermission()
//        }

//        horizontalChart = pageView.findViewById(R.id.highligte_today_Bar_Chart)
//        horizontal2Chart = pageView.findViewById(R.id.highligte_before_Bar_Chart)

//        horizontalChart.setNoDataText("")
//        initChart(horizontalChart)
//        initChart2(horizontal2Chart)
        //get() 값을 다 가져오는

//        var color : ImageView = pageView.findViewById(R.id.color)
//        getStudyInfo(color)

        return pageView
    }

//    val subjectList = MutableLiveData<DocumentSnapshot>()

//    private fun getStudyInfo(color: ImageView) {
//        FirebaseFirestore
//            .getInstance()
//            .collection("subject")
//            .document(LoginUtils.getUid()!!)
//            .get()
//            .addOnSuccessListener { ds
////                color.setBackgroundColor(Color.parseColor(it["color"] as String))
//                val tmp = hash
//            }
//    }



    private fun initChart(horizontalChart: HorizontalBarChart) {
//        with(horizontalChart) {
////            marker = customMarkerView
//            description.isEnabled = false
//            legend.isEnabled = false
//            isDoubleTapToZoomEnabled = false
//
//            setPinchZoom(false)
//            setDrawBarShadow(false)
//            setDrawValueAboveBar(false)
//
//            xAxis.setDrawGridLines(false)
//            axisLeft.isEnabled = false
//            axisRight.isEnabled = false
//            axisLeft.setDrawLabels(false)
//            axisRight.setDrawLabels(false)
//
//            //둥근 모서리 색상
//            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
//                setRadius(30)
//            }
//            renderer = barChartRender
//        }
        var barDataSet = BarDataSet(dataValue(), "")
        var color = ArrayList<Int>()
        color.add(Color.argb(100, 68, 158, 246))
        barDataSet.colors= color

        var barData = BarData(barDataSet)
        horizontalChart.data= barData
        horizontalChart.invalidate()
    }

    private fun dataValue(): ArrayList<BarEntry> {
        var dataValues = ArrayList<BarEntry>()
        dataValues.add(BarEntry(0.toFloat(),floatArrayOf(1.3f)))
        return dataValues
    }

    private fun initChart2(horizontal2Chart: HorizontalBarChart) {
        with(horizontal2Chart){//축 선 제거
//            description.isEnabled = false
//            legend.isEnabled = false
//            isDoubleTapToZoomEnabled = false
//
//            setPinchZoom(false)
//            setDrawBarShadow(false)
//            setDrawValueAboveBar(false)
//            //둥근 모서리 색상
//            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
//                setRadius(30)
//            }
//            renderer = barChartRender
//            //축 선 제거
//            xAxis.setDrawGridLines(false)
//            axisLeft.isEnabled = false
//            axisRight.isEnabled = false
            axisLeft.apply{
                axisMinimum= 0F
                granularity= 6F
                axisMaximum= 12F
            }
        }

        var barDataSet = BarDataSet(dataValue2(), "")
        var color = ArrayList<Int>()
        color.add(Color.argb(100, 68, 158, 246))
        barDataSet.colors= color

        var barData = BarData(barDataSet)
        horizontal2Chart.data= barData
        horizontal2Chart.invalidate()
    }

    private fun dataValue2(): ArrayList<BarEntry> {
        var dataValues = ArrayList<BarEntry>()
        dataValues.add(BarEntry(0.toFloat(),floatArrayOf(5.3f)))
        return dataValues
    }


}

