package kr.co.gooroomeelite.views.statistics

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import kr.co.gooroomeelite.adapter.DailySubjectAdapter
import kr.co.gooroomeelite.databinding.FragmentMonthBinding
import kr.co.gooroomeelite.viewmodel.SubjectViewModel
import kr.co.gooroomeelite.views.statistics.share.ShareActivity
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private val viewModel: SubjectViewModel by viewModels()

    private lateinit var chart: BarChart
    private val monthlySubjectAdapter: DailySubjectAdapter by lazy { DailySubjectAdapter(emptyList()) }


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

        divideDataFromFirebase()

        moveCalendarByDay(binding.calendarMonth,binding.calRightBtn,binding.calLeftBtn,binding.titleMonth)
        return binding.root
    }

    private fun divideDataFromFirebase() {

        viewModel.list.observe(viewLifecycleOwner) {
            val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val dateNow: String? = LocalDateTime.now().format(textformatter) //오늘
            //해당 월의 마지막 날짜 찾기
            val targetYearMonth: YearMonth = YearMonth.from(LocalDate.parse(dateNow.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            val end = targetYearMonth.atEndOfMonth()
            Log.d("asdf",end.toString())
            val monDay5 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay5 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay5 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay5 = end.with(DayOfWeek.THURSDAY) //목
            val friDay5 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay5 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay5 = end.with(DayOfWeek.SUNDAY) //일

            //일주일 간 데이터
            var mondays5Value : Float = 0f
            var tuesdays5Value: Float = 0f
            var wednesdays5Value: Float = 0f
            var thursdays5Value: Float = 0f
            var fridays5Value: Float = 0f
            var saturdays5Value: Float = 0f
            var sundays5Value: Float = 0f

            var mondaySum5: Float = 0f
            var tuesdaySum5: Float = 0f
            var wednesdaySum5: Float = 0f
            var thursdaySum5: Float = 0f
            var fridaySum5: Float = 0f
            var saturdaySum5: Float = 0f
            var sundaySum5: Float = 0f
            var totalSum5: Float = 0f

            val monDay4 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay4 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay4 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay4 = end.with(DayOfWeek.THURSDAY) //목
            val friDay4 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay4 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay4 = end.with(DayOfWeek.SUNDAY) //일

            //일주일 간 데이터
            var mondays4Value : Float = 0f
            var tuesdays4Value: Float = 0f
            var wednesdays4Value: Float = 0f
            var thursdays4Value: Float = 0f
            var fridays4Value: Float = 0f
            var saturdays4Value: Float = 0f
            var sundays4Value: Float = 0f

            var mondaySum4: Float = 0f
            var tuesdaySum4: Float = 0f
            var wednesdaySum4: Float = 0f
            var thursdaySum4: Float = 0f
            var fridaySum4: Float = 0f
            var saturdaySum4: Float = 0f
            var sundaySum4: Float = 0f
            var totalSum4: Float = 0f

            val cal = Calendar.getInstance()
            cal.get(Calendar.WEEK_OF_MONTH)
            Log.d("asdfadsf",cal.get(Calendar.WEEK_OF_MONTH).toString())//캘린더를 이용한 마지막 날 주 구하기

            val weekOfMonth: Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
            Log.d("asdfadsf",weekOfMonth.toString()) //마지막날의 주 사이즈 구하기






            var its : Int = 0
//            var subject: Subject? = null
            Log.d("asdgsdfg",its.toString())
            it.forEachIndexed { index, subject ->
                its = it.size
                val calen : Calendar = Calendar.getInstance()
                //서버에서 가져온 요일
                val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd")

                calen.add(Calendar.DATE,1)
                val serverDateFormat: String = dateFormat.format(subject.timestamp)
                val serverDateFormatPlus1: String = dateFormat.format(subject.timestamp?.day?.minus(1.toLong()))
//
                Log.d("calendar_day1_ss",serverDateFormat.toString())
                Log.d("calendar_day1_sssPlus",serverDateFormatPlus1.toString())


                for (it in 0..its) {
                    if (monDay5.format(textformatter) == serverDateFormat) {
                        mondays5Value = subject.studytimeCopy.toFloat()
                        mondaySum5 = mondaySum5 + mondays5Value
                        break
                    }else if(monDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        mondays4Value = subject.studytimeCopy.toFloat()
                        mondaySum4 = mondaySum4 + mondays4Value
                        break
                    }
                }
                for (it in 0..its) {
                    if (tuesDay5.format(textformatter) == serverDateFormat) {
                        tuesdays5Value = subject.studytimeCopy.toFloat()
                        tuesdaySum5 = tuesdaySum5 + tuesdays5Value
                        break
                    }else if(tuesDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        tuesdays4Value = subject.studytimeCopy.toFloat()
                        tuesdaySum4 = tuesdaySum4 + tuesdays4Value
                        break
                    }
                }
                for (it in 0..its) {
                    if (wednesDay5.format(textformatter) == serverDateFormat) {
                        wednesdays5Value = subject.studytimeCopy.toFloat()
                        Log.d("요일", wednesdays5Value.toString() + " 수")//21
                        Log.d("요일", subject.name.toString() + " : name")
                        wednesdaySum5 = wednesdaySum5 + wednesdays5Value
                        break
                    }else if(wednesDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        wednesdays4Value = subject.studytimeCopy.toFloat()
                        wednesdaySum4 = wednesdaySum4 + wednesdays4Value
                    }
                }
                for (it in 0..its) {
                    if (thursDay5.format(textformatter) == serverDateFormat) {
                        thursdays5Value = subject.studytimeCopy.toFloat()
                        Log.d("요일", thursdays5Value.toString() + " 목")//22
                        thursdaySum5 = thursdaySum5 + thursdays5Value
                        break
                    }else if(thursDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        thursdays4Value = subject.studytimeCopy.toFloat()
                        thursdaySum4 = thursdaySum4 + thursdays4Value
                        break
                    }
                }
                for (it in 0..its) {
                    if (friDay5.format(textformatter) == serverDateFormat) {
                        fridays5Value = subject.studytimeCopy.toFloat()
                        Log.d("요일", fridays5Value.toString() + " 금")//23
                        fridaySum5 = fridaySum5 + fridays5Value
                        break
                    }else if(friDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        fridays4Value = subject.studytimeCopy.toFloat()
                        fridaySum4 = fridaySum4 + fridays4Value
                        break
                    }
                }
                for (it in 0..its) {
                    if (saturDay5.format(textformatter)  == serverDateFormat) {
                        saturdays5Value = subject.studytimeCopy.toFloat()
                        Log.d("요일", saturdays5Value.toString() + " 토")//24
                        Log.d("요일", subject.name.toString() + " : name")
                        saturdaySum5 = saturdaySum5 + saturdays5Value
                        break
                    }else if(saturDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        saturdays4Value = subject.studytimeCopy.toFloat()
                        saturdaySum4 = saturdaySum4 + saturdays4Value
                        break
                    }
                }
                for (it in 0..its) {
                    if (sunDay5.format(textformatter) == serverDateFormat) {
                        sundays5Value = subject.studytimeCopy.toFloat()
                        Log.d("요일", sundays5Value.toString() + " --일--")//25
                        Log.d("요일", subject.name.toString() + " : name")
                        sundaySum5 = sundaySum5 + sundays5Value
                        break
                    }else if(sunDay4.minusWeeks(1).format(textformatter) == serverDateFormat){
                        sundays4Value = subject.studytimeCopy.toFloat()
                        sundaySum4 = sundaySum4 + sundays4Value
                        break
                    }
                }
            }

            totalSum5 = mondaySum5 + tuesdaySum5 + wednesdaySum5 + thursdaySum5 + fridaySum5 + saturdaySum5 + sundaySum5
            totalSum4 = mondaySum4 + tuesdaySum4 + wednesdaySum4 + thursdaySum4 + fridaySum4 + saturdaySum4 + sundaySum4
            Log.d("totalSum", (totalSum5/60).toString() + " 총합") //25
            Log.d("totalSum", (totalSum4/60).toString() + " 총합") //25
//            binding.weeklyTotalTime.text = "${(totalSum.toInt()) / 60}시간 ${(totalSum.toInt()) % 60}분"
//            val listData = mutableListOf<ChartData>(
//                ChartData(1,50f),
//                ChartData(2,100f)
//            )
//            setData(mondaySum, tuesdaySum, wednesdaySum, thursdaySum, fridaySum, saturdaySum, sundaySum, totalSum)
        }
    }

    private val listData by lazy {
        mutableListOf(
            //REd, green, blue
            //am 5 ~ 12
            ChartData(1, 50F),
            ChartData(2, 70F),
            ChartData(3, 80F),
            ChartData(4, 90F),
//            if(CharData.data. == null){
//                ChartData(5, 90F)
//            }

        )
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
        setData(listData)
    }

    private fun setData(barData: List<ChartData>) {
        val values = mutableListOf<BarEntry>()
//        values.add(BarEntry(0f, 100.1f))
//        values.add(BarEntry(1f, 100.1f))
//        values.add(BarEntry(2f, 100.1f))
//        values.add(BarEntry(3f, 100.1f))
//        values.add(BarEntry(4f, 100.1f))
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
                val xAxisLabels = listOf("첫째주", "둘째주", "셋째주", "넷째주", "다섯째주")
                valueFormatter = IndexAxisValueFormatter(xAxisLabels)
//                valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return barData[value.toInt()].date
//                    }
//                }
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
