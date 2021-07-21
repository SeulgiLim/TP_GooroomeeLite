package kr.co.gooroomeelite.views.statistics

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.MonthlySubjectAdapter
import kr.co.gooroomeelite.databinding.FragmentMonthBinding
import kr.co.gooroomeelite.viewmodel.SubjectViewModel
import kr.co.gooroomeelite.views.statistics.share.ShareActivity
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private val viewModel: SubjectViewModel by viewModels()

    private lateinit var chart: BarChart
    private val monthlySubjectAdapter: MonthlySubjectAdapter by lazy { MonthlySubjectAdapter(emptyList()) }


//     LocalDate 문자열로 포맷
//    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d")


    //아래,왼쪽 제목 이름
    private val ContentColor by lazy {
        ContextCompat.getColor(this.requireContext(), R.color.content_black)
    }

    //그래프 가로 축,선 (점선으로 변경)
    private val transparentBlackColor by lazy {
        ContextCompat.getColor(this.requireContext(), R.color.transparent_black)
    }

//    private val customMarkerView by lazy {
//        CustomMarketView(this.requireContext(), R.layout.item_marker_view)
//    }

    private val listData by lazy {
        mutableListOf(
            ChartData("첫째주", 100.1f),
            ChartData("둘째주", 140.5f),
            ChartData("셋째주", 168.5F),
            ChartData("넷째주", 160.5f),
            ChartData("다섯째주", 90.5f),
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMonthBinding.inflate(inflater,container,false)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_month,container,false)
        binding.month = this

        binding.shareButton.setOnClickListener {
            requestPermission()
        }
        //바 차트
        binding.monthBarChart.setNoDataText("")
        initChart(binding.monthBarChart)
        binding.monthBarChart.setVisibleXRangeMaximum(30f)
        binding.monthBarChart.moveViewToX(30f)

        monthlySubjectPieChart()
        binding.recyclerViewMonth.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = monthlySubjectAdapter
        }

        moveCalendarByDay(binding.calendarMonth,binding.calRightBtn,binding.calLeftBtn,binding.titleMonth)
        return binding.root
    }

    private fun initChart(chart: BarChart) {
//        customMarkerView.chartView = chart
        with(chart) {
//            marker = customMarkerView
            description.isEnabled = false
            legend.isEnabled = false
            isDoubleTapToZoomEnabled = false
            
//            setFitBars(true) //X축을 모든 막대에 정확히 맞춘다.
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(false)
            //둥근 모서리 색상
            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
//                setRadius(10)
            }
            renderer = barChartRender
        }
        setDatas(listData)
    }

    private fun setDatas(barData : List<ChartData>) {
        val values = mutableListOf<BarEntry>()
        barData.forEachIndexed { index, chartData ->
            values.add(BarEntry(index.toFloat(), chartData.value))

        }


        //막대 그래프 색상 추가
        val barDataSet = BarDataSet(values, "").apply {
            //각 데이터의 값을 텍스트 형식으로 나타내지 않게  (y값 그리기가 활성화되어 있으면 true를 반환하고 그렇지 않으면 false를 반환한다.)
            setDrawValues(false)

            val colors = ArrayList<Int>()
            colors.add(Color.parseColor("#FF339BFF"))
            setColors(colors)
            highLightAlpha = 0
        }

        //막대 그래프 너비 설정
        val dataSets = mutableListOf(barDataSet)
        val data = BarData(dataSets as List<IBarDataSet>?).apply {
//            setValueTextSize(30F)
            barWidth = 0.1F
        }
        with(binding.monthBarChart) {
            animateY(1000)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = ContentColor
                //밑에 코드는 막대차트와 x축 중앙 연결
                isGranularityEnabled = true // 세분성 활성화됨
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return barData[value.toInt()].date
                    }
                }
            }

            //차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
            axisRight.apply {
                textColor = ContentColor
                setDrawAxisLine(false) //격자
                gridLineWidth = 1F
                gridColor = transparentBlackColor
                axisLineColor = transparentBlackColor //축의 축선 색상
                enableGridDashedLine(5f,5f,5f)

                axisMaximum = 168F
                granularity = 42F
                axisMinimum = 0F
                setLabelCount(4,true) //축 고정간격

                //y축 제목 커스텀
                valueFormatter = object : ValueFormatter() {
                    private val mFormat: DecimalFormat = DecimalFormat("###")
                    override fun getFormattedValue(value: Float): String {
                        return mFormat.format(value) + "시간"
                    }
                }
            }

            //차트 오른쪽 축, Y방향 false처리
            axisLeft.apply {
                isEnabled = false
                gridLineWidth = 1F
                gridColor = ContentColor
                axisLineColor = transparentBlackColor //축의 축선 색상
//                labelPosition = floatArrayOf(0f, 10f, 20f, 50f, 100f, 300f)
//                setSpecificLabelPositions(floatArrayOf(0f, 10f, 20f, 50f, 100f, 300f))
                axisMaximum = 168F
                granularity = 42F
                axisMinimum = 0F
                setLabelCount(4, true) //축 고정간격
            }

            notifyDataSetChanged()  //chart의 값 변동을 감지함
            this.data = data
            invalidate()
        }
    }

    private fun moveCalendarByDay(calendarMonth:TextView,calRightBtn:ImageButton,calLeftBtn:ImageButton,title:TextView){
        // 현재 날짜/시간 가져오기
        val dateNow: LocalDate = LocalDate.now()
        val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM")
        val titleformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월달에")

        var count : Int = 0
        calendarMonth.text = dateNow.format(textformatter) //하루 2021.07.08

        dateNow.plusDays(count.toLong()) //일간탭으로 돌아왔을 때 오늘 날짜로 다시 변경
        calRightBtn.setOnClickListener {
            count++
            if(count == 1){
                calRightBtn.isEnabled = false
            }else {
                val dayPlus: LocalDate = dateNow.plusMonths(count.toLong())
                calendarMonth.text = dayPlus.format(textformatter).toString()
                if (count == 0) {
                    title.text = "이번 달에"
                } else if (count == -1) {
                    calRightBtn.isEnabled = true
                    title.text = "지난 달에"
                } else {
                    title.text = dayPlus.format(titleformatter).toString()
                }
            }
        }

        calLeftBtn.setOnClickListener {
            count--
            val minusDay: LocalDate = dateNow.plusMonths(count.toLong())
            calendarMonth.text =  minusDay.format(textformatter).toString()
            if (count == 0) {
                title.text = "이번 달에"
            } else if (count == -1) {
                title.text = "지난 달에"
                calRightBtn.isEnabled = true
            } else {
                title.text = minusDay.format(titleformatter).toString()
            }
        }
    }

    private fun monthlySubjectPieChart(){
        viewModel.list.observe(viewLifecycleOwner) {
            val pieChart: PieChart = binding.monthlyPieChart
            pieChart.setUsePercentValues(true)
            val values = mutableListOf<PieEntry>()
            val colorItems = mutableListOf<Int>()
            it.forEach{
                values.add(PieEntry(it.studytime.toFloat(),it.name.toString()))
            }
            it.forEachIndexed { index, subject ->
                colorItems.add(index,Color.parseColor(subject.color))
            }

            val pieDataSet = PieDataSet(values, "")
            pieDataSet.colors = colorItems
            pieDataSet.apply {
//            valueTextColor = Color.BLACK
                setDrawValues(false) //차트에 표시되는 값 지우기
                valueTextSize = 16f
            }
            //% : 퍼센트 수치 색상과 사이즈 지정
            val pieData = PieData(pieDataSet)
            pieChart.apply {
                data = pieData
                description.isEnabled = false //해당 그래프 오른쪽 아래 그래프의 이름을 표시한다.
                isRotationEnabled = false //그래프를 회전판처럼 돌릴 수 있다
//            centerText = "this is color" //그래프 한 가운데 들어갈 텍스트
//            setEntryLabelColor(Color.RED) //그래프 아이템의 이름의 색 지정
                isEnabled = false
                legend.isEnabled = false //범례 지우기
                isDrawHoleEnabled = true //중앙의 흰색 테두리 제거
                holeRadius = 50f //흰색을 증앙에 꽉 채우기
                setDrawEntryLabels(false) //차트에 있는 이름 지우
                animateY(1400, Easing.EaseInOutQuad)
                animate()
            }
        }

    }
    //adapter에 데이터 추가
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.subjectList.observe(viewLifecycleOwner) {
            monthlySubjectAdapter.setData(it)
        }
    }

    private fun requestPermission(): Boolean {
        var permissions = false
        TedPermission.with(context)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    permissions = true      //p0=response(응답)
                    val shareIntent = Intent(context, ShareActivity::class.java)
                    startActivity(shareIntent)
//                    finish()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    permissions = false
                }

            })
            .setDeniedMessage("앱을 실행하려면 권한을 허가하셔야합니다.")
            .setPermissions(Manifest.permission.CAMERA)
            .check()
        return permissions
    }

}
