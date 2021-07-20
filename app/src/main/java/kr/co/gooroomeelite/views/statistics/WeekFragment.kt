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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.WeeklySubjectAdapter
import kr.co.gooroomeelite.databinding.FragmentWeekBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.viewmodel.SubjectViewModel
import kr.co.gooroomeelite.views.statistics.share.ShareActivity
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.O)
class WeekFragment : Fragment() {
    private lateinit var binding: FragmentWeekBinding
    private val viewModel: SubjectViewModel by viewModels()
    private val weeklySubjectAdapter: WeeklySubjectAdapter by lazy { WeeklySubjectAdapter(emptyList()) }
    private var list: MutableList<Subject> = mutableListOf()
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

    //   LocalDate 문자열로 포맷
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E")

    private var subjectListValue: MutableList<Subject> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week, container, false)
        binding.week = this

        binding.weekBarChart.setNoDataText("")

        initChart()

        weeklySubjectPieChart()
        binding.shareButton.setOnClickListener {
            requestPermission()
        }

        pieChartRecyclerView()
        //주간별 시간 캘린더 이동 버튼 화면
        moveCalendarByWeek(
            binding.calendarMonday,
            binding.calendarSunday,
            binding.calRightBtn,
            binding.calLeftBtn,
            binding.titleWeek,
            binding.titleWeekNext
        )
        return binding.root
    }
    private fun moveCalendarByWeek(monDay: TextView, sunDay: TextView, rBtn: ImageButton, lBtn: ImageButton, title: TextView, titleNext: TextView){
        val dateNow: LocalDateTime = LocalDateTime.now()

        val cal = Calendar.getInstance()
        val monday: LocalDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY)//해당 주차의 월요일
        val sunday: LocalDateTime = LocalDateTime.now().with(DayOfWeek.SUNDAY)

        val firstDayOfWeek: LocalDate = LocalDate.now() //현재 날짜
        val fieek: Int = firstDayOfWeek.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
        val firstDay: LocalDate = firstDayOfWeek.withDayOfMonth(1) //매월 첫 날
        val endDay: LocalDate =
            firstDayOfWeek.withDayOfMonth(firstDayOfWeek.lengthOfMonth())// 매월 마지막 날
        val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val weektextformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월") //7월
//        val weekend: Int = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_MONTH)//몇째주

        var count: Int = 0
        var countOfWeek: Int = 0

        monDay.text = monday.format(textformatter)
        sunDay.text = sunday.format(textformatter)


        rBtn.setOnClickListener {
            count++
            //2021.07.19 ~ 2021.07.25 표시
            val mondayValue: LocalDateTime = monday.plusWeeks(count.toLong())
            monDay.text = mondayValue.format(textformatter).toString()
            val sundayValue: LocalDateTime = sunday.plusWeeks(count.toLong())
            sunDay.text = sundayValue.format(textformatter).toString()

            if (count == 0) {
                title.text = "이번"
                titleNext.text = "주에"
            } else if (count == -1) {
                title.text = "지난"
                titleNext.text = "주에"
            } else {  //ALIGNED_WEEK_OF_MONTH : 그 달의 n 번째 주
                titleNext.text =
                    mondayValue.get(ChronoField.ALIGNED_WEEK_OF_MONTH).toString() + "째 주에"
                title.text = mondayValue.format(weektextformatter).toString()
            }

        }


        lBtn.setOnClickListener {
            count--
            //2021.07.19 ~ 2021.07.25 표시
            val sundayValue: LocalDateTime = sunday.plusWeeks(count.toLong())
            sunDay.text = sundayValue.format(textformatter).toString()
            val mondayValue: LocalDateTime = monday.plusWeeks(count.toLong())
            monDay.text = mondayValue.format(textformatter).toString()

            if (count == 0) {
                title.text = "이번"
                titleNext.text = "주에"
            } else if (count == -1) {
                title.text = "지난"
                titleNext.text = "주에"
            } else {
                titleNext.text =
                    mondayValue.get(ChronoField.ALIGNED_WEEK_OF_MONTH).toString() + "째 주에"
                title.text = mondayValue.format(weektextformatter).toString()
            }
        }
    }
    private fun initChart() {
//        customMarkerView.chartView = chart
        with(binding.weekBarChart) {
//            marker = customMarkerView
            description.isEnabled = false
            legend.isEnabled = false
            isDoubleTapToZoomEnabled = false

            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(false)
            //둥근 모서리 색상
            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
//                setRadius(20)
            }
            renderer = barChartRender
        }
        setData()
    }
    private fun setData() {
        val values = mutableListOf<BarEntry>()
        values.add(BarEntry(0f, 17f))
        values.add(BarEntry(1f, 19f))
        values.add(BarEntry(2f, 19f))
        values.add(BarEntry(3f, 17f))
        values.add(BarEntry(4f, 18f))
        values.add(BarEntry(5f, 18f))
        values.add(BarEntry(6f, 19f))

        //막대 그래프 색상 추가
        val barDataSet = BarDataSet(values, "").apply {
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
            barWidth = 0.2F
        }

        with(binding.weekBarChart) {
            val ll = LimitLine(18.2f, "평균").apply {
                lineColor = Color.BLACK
                lineWidth = 1f
                textColor = Color.BLACK
                textSize = 12f
                labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
                enableDashedLine(5f,5f,15f)
            }
            animateY(100)
            //x축을 나타냄
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false) //축 점선
                textColor = whiteColor

                //월 ~ 일 (x축 label 이름)
                val xAxisLabels = listOf("월", "화", "수", "목", "금", "토", "일")
                valueFormatter = IndexAxisValueFormatter(xAxisLabels)

            }

            //차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
            axisRight.apply {
                addLimitLine(ll)
                textColor = whiteColor
                setDrawAxisLine(false) //격자
                gridColor = transparentBlackColor
                gridLineWidth = 0.5F
                enableGridDashedLine(5f, 5f, 5f)

                axisMaximum = 24F //최대값
                granularity = 3F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                axisMinimum = 0F //최소값
                setLabelCount(4, true) //축 고정간격
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
                addLimitLine(ll)
                isEnabled = false
                gridColor = transparentBlackColor

                axisMaximum = 24F //최대값
                granularity = 3F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                axisMinimum = 0F //최소값
                setLabelCount(4, true) //축 고정간격
            }

            notifyDataSetChanged()
            this.data = data
            invalidate()
        }
    }
    //주간별 원 차트
    private fun weeklySubjectPieChart() {
        val pieChart: PieChart = binding.weeklyPieChart
        pieChart.setUsePercentValues(true)
        val values = mutableListOf<PieEntry>()
        val colorItems = mutableListOf<Int>()
        viewModel.list.observe(viewLifecycleOwner) {
//            it.forEachIndexed{index, subject ->
//                subjectListValue.add(index,subject)
//            }

            it.forEach {
                values.add(PieEntry(it.studytime.toFloat(), it.name.toString()))
            }
            it.forEachIndexed { index, subject ->
                colorItems.add(index, Color.parseColor(subject.color))
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
        Log.d("zxcvzxcvzzz", list.size.toString())
        Log.d("zxcvzxcvzzz", list.toString())
    }
    private fun pieChartRecyclerView() {
        subjectListValue.forEach {
            Log.d("gtguvsdf", "it.color.toString()")
            Log.d("gtguvsdf", it.color.toString())
            Log.d("gtguvsdf", it.studytime.toString())
        }
        binding.recyclerViewWeek.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = weeklySubjectAdapter
        }
    }
    //adapter에 데이터 추가
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.subjectList.observe(viewLifecycleOwner) {
            weeklySubjectAdapter.setData(it)
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

