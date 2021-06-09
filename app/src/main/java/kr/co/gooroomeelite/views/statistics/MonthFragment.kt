package kr.co.gooroomeelite.views.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kr.co.gooroomeelite.R

class MonthFragment : Fragment() {

    private lateinit var chart: BarChart

    private val listData by lazy {
        mutableListOf(
            ChartData("1일", 5.1F), ChartData("", 10F), ChartData("", 8.1F), ChartData("", 7.1F), ChartData("", 6.1F), ChartData("", 5.1F), ChartData("", 9.1F),
            ChartData("", 3.1F), ChartData("", 11F), ChartData("", 8.1F), ChartData("", 7.1F), ChartData("", 8.1F), ChartData("", 5.1F), ChartData("", 9.1F),
            ChartData("15일", 3.1F), ChartData("", 6.4F), ChartData("", 8.1F), ChartData("", 7.1F), ChartData("", 1.1F), ChartData("", 5.1F), ChartData("", 9.1F),
            ChartData("", 7.1F), ChartData("", 9.3F), ChartData("", 8.2F), ChartData("", 7.1F), ChartData("", 7F), ChartData("", 5.1F), ChartData("", 9.1F),
            ChartData("29일", 5F), ChartData("", 5F), ChartData("", 5F)
        )
    }

//    private fun listDataValue(){
//        var i : Int = 1
//        while (i <= 31) {
//            val name : String = ""
//            mutableListOf(
//                ChartData(name,(Math.random()*16).toFloat())
//            )
//            i++
//            return
//        }
//    }

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_month, container, false)

        //바 차트
        chart = view.findViewById(R.id.month_bar_chart)
        chart.setNoDataText("")
        initChart(chart)
        return view
    }

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
                setRadius(10)
            }
            renderer = barChartRender
        }
        setData(listData)
    }

    private fun setData(barData: List<ChartData>) {
        val values = mutableListOf<BarEntry>()
        barData.forEachIndexed { index, chartData ->
            values.add(BarEntry(index.toFloat(), chartData.value))
        }

        //막대 그래프 색상 추가
        val barDataSet = BarDataSet(values, "").apply {
            //각 데이터의 값을 텍스트 형식으로 나타내지 않게  (y값 그리기가 활성화되어 있으면 true를 반환하고 그렇지 않으면 false를 반환한다.)
            setDrawValues(false)

            val colors = ArrayList<Int>()
            colors.add(Color.argb(55,61,171,91))
            setColors(colors)
            highLightAlpha = 0
        }

        //막대 그래프 너비 설정
        val dataSets = mutableListOf(barDataSet)
        val data = BarData(dataSets as List<IBarDataSet>?).apply {
//            setValueTextSize(30F)
            barWidth = 0.5F
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
                var chartDataMax = listData.maxBy { it -> it.value}
                var maxValue = chartDataMax!!.value
                Log.d("aaa","$maxValue")
                barData.forEachIndexed{ index, chartData ->
                    while(chartData.value > axisMaximum){
                        count++
                        if(chartData.value > axisMaximum){
                            axisMaximum = maxValue
                        }else{
                            axisMaximum = 9F
                        }
                    }

                }
                axisMinimum = 0F
//                axisMaximum = 9F
                granularity  = 3F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
            }

            //차트 오른쪽 축, Y방향 false처리
            axisLeft.apply {
                isEnabled = false
                gridColor = transparentBlackColor
                var count = 0
                //차트데이터 값에서 가장 큰 값
                var chartDataMax = listData.maxBy { it -> it.value}
                var maxValue = chartDataMax!!.value
                Log.d("aaa","$maxValue")
                barData.forEachIndexed{ index, chartData ->
                    while(chartData.value > axisMaximum){
                        count++
                        if(chartData.value > axisMaximum){
                            axisMaximum = maxValue
                        }else{
                            axisMaximum = 9F
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