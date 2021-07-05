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
import java.text.DecimalFormat

class DayFragment : Fragment() {

    private lateinit var chart: BarChart

    private val listData by lazy {
        mutableListOf(
            ChartDatas("오전 12시", arrayListOf(0F)), ChartDatas("", arrayListOf(0F)), ChartDatas("", arrayListOf(0F)),
                ChartDatas("", arrayListOf(0F)), ChartDatas("", arrayListOf(0F)), ChartDatas("", arrayListOf(0F)),
                //am 6t ~ am 12t
            ChartDatas("오전 6시", arrayListOf(25.1F,35f,47.1f)), ChartDatas("", arrayListOf(3f,5f,10F)), ChartDatas("", arrayListOf(10f,38.1F,46f,50f)),
            ChartDatas("", arrayListOf(35f,60f)), ChartDatas("", arrayListOf(5f,10F,48f)), ChartDatas("", arrayListOf(48.1F,20f)),
            //pm 12t ~ pm 6t
            ChartDatas("오후 6시", arrayListOf(0F)), ChartDatas("", arrayListOf(0F)), ChartDatas("", arrayListOf(10f,20f,28.1F,45f,60f)),
            ChartDatas("", arrayListOf(45.1F,50f,60f,10f)), ChartDatas("", arrayListOf(10F)), ChartDatas("", arrayListOf(48.1F,20f)),
            //pm 6t ~ pm 12t
            ChartDatas("오후 12시", arrayListOf(5.1F,10f,15f,20f,25f,30f)), ChartDatas("", arrayListOf(10F,20f,30f,40f,50f)), ChartDatas("", arrayListOf(18.1F)),
            ChartDatas("", arrayListOf(35.1F)), ChartDatas("", arrayListOf(10F,40f)), ChartDatas("", arrayListOf(28.1F,40f,10f,5f)),
            )
    }


    //아래,왼쪽 제목 이름
    private val whiteColor by lazy {
        ContextCompat.getColor(this.requireContext(), R.color.black)
    }

    //그래프 가로 축,선 (점선으로 변경)
    private val transparentBlackColor by lazy {
        ContextCompat.getColor(this.requireContext(), R.color.black)
    }

    private val customMarkerView by lazy {
        CustomMarketView(this.requireContext(), R.layout.item_marker_view)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_day, container, false)

        //바 차트
        chart = view.findViewById(R.id.day_bar_chart)
        chart.setNoDataText("")
        initChart(chart)
        return view
    }

    private fun initChart(chart: BarChart) {
        customMarkerView.chartView = chart
        with(chart) {
            marker = customMarkerView
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

    private fun setData(barData: List<ChartDatas>) {
        val values = mutableListOf<BarEntry>()
        barData.forEachIndexed { index, chartData ->
            //첫번째 인자 x , 두번째 인자 y
            for(i in chartData.value){
                values.add(BarEntry(index.toFloat(), i))
            }
        }

        //막대 그래프 색상 추가
        val barDataSet = BarDataSet(values, "").apply {
            //각 데이터의 값을 텍스트 형식으로 나타내지 않게  (y값 그리기가 활성화되어 있으면 true를 반환하고 그렇지 않으면 false를 반환한다.)
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
//            barWidth = 0.5F
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
                barData.forEachIndexed { index, chartData ->
                    for (i in chartData.value) {
                        var maxValue = i
                        Log.d("aaa", "$maxValue")
                        barData.forEachIndexed { index, chartData ->
                            while (i > axisMaximum) {
                                count++
                                if (i > axisMaximum) {
                                    axisMaximum = maxValue
                                } else {
                                    axisMaximum = 60F
                                }
                            }
                        }
                    }
                    axisMinimum = 0F
                    granularity = 20F

                    //y축 제목 커스텀
                    valueFormatter = object : ValueFormatter() {
                        private val mFormat: DecimalFormat = DecimalFormat("###")
                        override fun getFormattedValue(value: Float): String {
                            return mFormat.format(value) + "분"
                        }
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
                                axisMaximum = 60F
                            }
                        }
                    }
                }
                axisMinimum = 0F
                granularity = 20F
            }

            notifyDataSetChanged()
            this.data = data
            invalidate()
        }
    }
//    val subject = intent.getSerializableExtra("subject") as Subject
//    val documentId = intent.getSerializableExtra("documentId") as String
//    Log.d("subject", subject.toString())
//    Log.d("documentId", documentId)
}