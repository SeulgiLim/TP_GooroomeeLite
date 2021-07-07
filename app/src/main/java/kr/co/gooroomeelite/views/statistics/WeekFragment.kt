package kr.co.gooroomeelite.views.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.FragmentWeekBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import kr.co.gooroomeelite.viewmodel.SubjectViewModel
import org.w3c.dom.Document
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class WeekFragment : Fragment() {
    private lateinit var binding : FragmentWeekBinding
    private val viewModel: SubjectViewModel by viewModels()
    private lateinit var chart: BarChart
    private val myStudyTime = MutableLiveData<Int>()


    //아래,왼쪽 제목 이름
    private val whiteColor by lazy {
        ContextCompat.getColor(this.requireContext(), R.color.black)
    }

    //그래프 가로 축,선 (점선으로 변경)
    private val transparentBlackColor by lazy {
        ContextCompat.getColor(this.requireContext(), R.color.black)
    }

//    private val customMarkerView by lazy {
//        CustomMarketView(this.requireContext(), R.layout.item_marker_view)
//    }

    private val listData by lazy {
        mutableListOf(
            ChartDatas("월", arrayListOf(1.5f,6.1f,3.3f,4.4f)), //5
            ChartDatas("화", arrayListOf(2.1f)),                 //5
            ChartDatas("수", arrayListOf(3.0f,5.5f,6.6f)),        //4
            ChartDatas("목", arrayListOf(3f,5.1f,3.5f)),             //3
            ChartDatas("금", arrayListOf(6.1f,4.5f,10.1f,8.5f)),      //3
            ChartDatas("토", arrayListOf(5f,7.1f,5.5f)),              //3
            ChartDatas("일", arrayListOf(8.1f,6.5f)),                //2
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_week, container, false)

        //바 차트
        chart = view.findViewById(R.id.week_bar_chart)
        chart.setNoDataText("")
        initChart(chart)
//        setting()
        return view
    }
    private lateinit var subjects : List<DocumentSnapshot>
    private val studyTime = MutableLiveData<Int>()
//    val uid :String

//    init{
//        uid = currentUser()!!.uid
//    }
//
//
//    private fun setting() {
//        FirebaseFirestore.getInstance().collection("subject")
//             .whereEqualTo("uid",uid)
//            .addSnapshotListener{ value,error ->
//            if(error != null){
//                return@addSnapshotListener
//            }
//                if (value != null) {
//                    value.documents.forEach {
//                        val tmp = hashMapOf<String,DocumentSnapshot>()
//                        if(it["studyTime"] as String? != null) {
//                            Log.d("cccaa", it.toString())
//                        }
//                    }
//                }
//
//        }
//    }

    private fun initChart(chart: BarChart) {
//        customMarkerView.chartView = chart
        with(chart) {
//            marker = customMarkerView
            description.isEnabled = false
            legend.isEnabled = false
            isDoubleTapToZoomEnabled = false

            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(false)
            //둥근 모서리 색상
            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
                setRadius(20)
            }
            renderer = barChartRender
        }
        setData(listData)
    }

    private fun setData(barData: List<ChartDatas>) {

        val values = mutableListOf<BarEntry>()

        barData.forEachIndexed { index, chartData ->
            //첫번째 인자 x , 두번째 인자 y
            for(i in chartData.value){
                values.add(BarEntry(index.toFloat(), i))
            }
        }

        //해당 entries를 그래프에 설정해준다
        //막대 그래프 색상 추가
        val barDataSet = BarDataSet(values, "").apply {
            setDrawValues(false)

            val colors = ArrayList<Int>()
            colors.add(Color.argb(100,68,158,246))
            setColors(colors)
            highLightAlpha = 0
        }

        //막대 그래프 너비 설정
        val dataSets = mutableListOf(barDataSet)
        val data = BarData(dataSets as List<IBarDataSet>?).apply {
//            setValueTextSize(30F)
            barWidth = 0.3F
        }
        //애니메이션 효과 0.1초
        with(chart) {
            animateY(100)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = whiteColor
                //월 ~ 일
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return barData[value.toInt()].date
                    }
                }
            }
            //차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
            axisRight.apply {
                textColor = whiteColor
                setDrawAxisLine(false) //격자
                gridColor = transparentBlackColor
                gridLineWidth = 0.5F
               enableGridDashedLine(5f,5f,5f)

                var count = 0
                //차트데이터 값에서 가장 큰 값
                barData.forEachIndexed { index, chartData ->
                    for (i in chartData.value) {
//                        var chartDataMax = listData.maxBy { it -> it. }
                        var maxValue = i
                        Log.d("aaa", "$maxValue"+"maxValue값")
                        barData.forEachIndexed { index, chartData ->
                            while (i > axisMaximum) {
                                count++
                                if (i > axisMaximum) {
                                    axisMaximum = maxValue
                                } else {
                                    axisMaximum = 9F
                                }
                            }
                        }
                    }
                }
                axisMinimum = 0F
//                axisMaximum = 9F
                granularity  = 3F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                //y축 제목 커스텀
                valueFormatter = object : ValueFormatter() {
                    private val mFormat: DecimalFormat = DecimalFormat("###")
                    override fun getFormattedValue(value: Float): String {
                        return mFormat.format(value) + "시"
                    }
                }
            }

            //차트 오른쪽 축, Y방향 false처리
            axisLeft.apply {
                isEnabled = false
                gridColor = transparentBlackColor
                var count = 0
                //차트데이터 값에서 가장 큰 값
//                var chartDataMax = listData.maxBy { it -> it.value}
                barData.forEachIndexed { index, chartData ->
                    for (i in chartData.value) {
//                        var chartDataMax = listData.maxBy { it -> it. }
                        var maxValue = i
                        Log.d("aaa", "$maxValue")
                        while (i > axisMaximum) {
                            count++
                            if (i > axisMaximum) {
                                axisMaximum = maxValue
                            } else {
                                axisMaximum = 9F
                            }
                        }
                    }
                }
                axisMinimum = 3F
//                axisMaximum = 9F

            }


            notifyDataSetChanged()
            this.data = data
            invalidate()
        }
    }

}