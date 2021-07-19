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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.DailySubjectAdapter
import kr.co.gooroomeelite.databinding.FragmentWeekBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.entity.Subjects
import kr.co.gooroomeelite.utils.LoginUtils
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
    private lateinit var binding : FragmentWeekBinding
    private val viewModel: SubjectViewModel by viewModels()
    private val dailySubjectAdapter: DailySubjectAdapter by lazy { DailySubjectAdapter(emptyList()) }


    //db값 저장
//    private lateinit var subjects: Subjects
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

    // 현재 날짜/시간 가져오기
    val dateNow: LocalDateTime = LocalDateTime.now(     )
    //     LocalDate 문자열로 포맷
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E")
//    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekBinding.inflate(inflater,container,false)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_week,container,false)
        binding.week = this

        binding.weekBarChart.setNoDataText("")

        initChart()

        weeklySubjectPieChart()
        binding.shareButton.setOnClickListener {
            requestPermission()
        }

        pieChartRecyclerView()
        //주간별 시간 캘린더 이동 버튼 화면
        moveCalendarByWeek(binding.calendarMonday,binding.calendarSunday,binding.calRightBtn,binding.calLeftBtn,binding.titleWeek,binding.titleWeekNext)
        return binding.root
    }


    private fun moveCalendarByWeek(monDay:TextView,sunDay:TextView,rBtn:ImageButton,lBtn:ImageButton,title: TextView,titleNext: TextView) {
        val calendarWeekNow: LocalDateTime = LocalDateTime.now()

        val cal = Calendar.getInstance()
        val weeks : Int = cal.get(Calendar.WEEK_OF_MONTH)
//        val weeks : String = cal.get(Calendar.MONTH).plus(7).toString()  //7월달(+1)
        val monday: LocalDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY)//해당 주차의 월요일
        val sunday: LocalDateTime = LocalDateTime.now().with(DayOfWeek.SUNDAY)


        val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val weektextformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월") //7월
//        val weekend: Int = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_MONTH)//몇째주

        var count: Int = 0

        monDay.text = monday.format(textformatter)
        sunDay.text = sunday.format(textformatter)


        rBtn.setOnClickListener {
            count++
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
            } else {
                title.text = calendarWeekNow.format(weektextformatter)
                titleNext.text = monday.plusWeeks(count.toLong()).get(ChronoField.ALIGNED_WEEK_OF_MONTH).toString() + "째 주에"

            }
        }

        lBtn.setOnClickListener {
            count--
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
                title.text = calendarWeekNow.format(weektextformatter)
                titleNext.text = mondayValue.plusWeeks(count.toLong()).get(ChronoField.ALIGNED_WEEK_OF_MONTH).toString() + "째 주에"
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
            values.add(BarEntry(0f, 15f))
            values.add(BarEntry(1f, 16f))
            values.add(BarEntry(2f, 17f))
            values.add(BarEntry(3f, 18f))
            values.add(BarEntry(4f, 19f))
            values.add(BarEntry(5f, 11f))
            values.add(BarEntry(6f, 12f))

            //막대 그래프 색상 추가
            val barDataSet = BarDataSet(values, "").apply {
                setDrawValues(false)

                val colors = ArrayList<Int>()
                colors.add(Color.argb(100, 51, 155, 255))
                setColors(colors)
                highLightAlpha = 0
            }

            //막대 그래프 너비 설정
            val dataSets = mutableListOf(barDataSet)
            val data = BarData(dataSets as List<IBarDataSet>?).apply {
//            setValueTextSize(30F)
                barWidth = 0.2F
            }

            //애니메이션 효과 0.1초
            with(binding.weekBarChart) {
                animateY(100)
                //x축을 나타냄
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false) //축 점선
                    textColor = whiteColor

                    //월 ~ 일 (x축 label 이름)
                    val xAxisLabels = listOf("월", "화", "수", "목", "금", "토","일")
                    valueFormatter = IndexAxisValueFormatter(xAxisLabels)

                }
//            axisLeft.labelPosition = true
//            axisLeft.labelCount =
//            axisLeft.setPosition(floatArrayOf(0f, 10f, 20f, 50f, 100f, 300f))
                //차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
                axisRight.apply {
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
                    isEnabled = false
                    gridColor = transparentBlackColor

                    axisMaximum = 24F //최대값
                    granularity = 3F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                    axisMinimum = 0F //최소값
                    setLabelCount(4, true) //축 고정간격
                }

                notifyDataSetChanged()
                this.data = data
//                binding.weekBarChart.setVisibleXRangeMaximum(7f)
//                moveViewToX(-10f)
                invalidate()
            }
        }

        //주간별 원 차트
//        private fun weeklySubjectPieChart(pieChart: PieChart, list: MutableList<Subjects>) {
        private fun weeklySubjectPieChart() {
           val pieChart : PieChart = binding.weeklyPieChart
            pieChart.setUsePercentValues(true)
            val values = mutableListOf<PieEntry>()
            val colorItems = mutableListOf<Int>()
            viewModel.list.observe(viewLifecycleOwner){
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
            Log.d("zxcvzxcvzzz",list.size.toString())
            Log.d("zxcvzxcvzzz",list.toString())
        }

        private fun pieChartRecyclerView() {
            binding.recyclerViewWeek.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = dailySubjectAdapter
            }
        }
        //adapter에 데이터 추가
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.subjectList.observe(viewLifecycleOwner) {
            dailySubjectAdapter.setData(it)
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

//    private fun  weelkyTotalHourTitle(list:MutableList<Subjects>,subjects: Subjects){
//        val calendarWeekNow : LocalDateTime = LocalDateTime.now()
//        val monday : LocalDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY)//해당 주차의 월요일
//        val sunday : LocalDateTime = LocalDateTime.now().with(DayOfWeek.SUNDAY)
//        Log.d("localDataTime",monday.toString())
//        Log.d("localDataTime",sunday.toString())
//        Log.d("localDataTime",list.get(5).toString())
//        Log.d("localDataTime",subjects.timestamp.toString())
//    }

//  setDate()
//차트가 7개만 보이게 세
//        chart.setVisibleXRangeMaximum(7f)
//        chart.setVisibleXRangeMinimum(7f)팅//스크롤 중지??
//현재 창의 왼쪽을 지정된 x 축 위치로 이동합니다.
//        chart.moveViewToX(-10f)
//        chart.moveViewTo(100f,0f, YAxis.AxisDependency.LEFT)
//        val shareButton: Button = view.findViewById(R.id.share_button)
//            setVisibleXRangeMaximum(7f)
//            chart.setVisibleXRangeMaximum(7f)
//        chart.setVisibleXRangeMinimum(7f)팅//스크롤 중지??

//시간 수정
//    val cal = Calendar.getInstance()
//    cal.get(Calendar.WEEK_OF_MONTH)
//    //        val month : String = cal.get(Calendar.ALIGNED_WEEK_OF_MONTH).toString()//3주차
////        binding.a.text = month
//    val weeks : String = cal.get(Calendar.MONTH).plus(7).toString()  //7월달(+1)
////        binding.s.text = weeks
////        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
////        cal.time = df.parse("2019-07-04")

//BEFORE CODE
//class WeekFragment : Fragment() {
//    private lateinit var binding : FragmentWeekBinding
//    private val viewModel: SubjectViewModel by viewModels()
//    //    private lateinit var chart: BarChart
//    private val myStudyTime = MutableLiveData<Int>()
//
//    private val dailySubjectAdapter: DailySubjectAdapter by lazy { DailySubjectAdapter(emptyList()) }
//
//
//    //db값 저장
//    private lateinit var subjects: Subjects
//    private var list: MutableList<Subjects> = mutableListOf()
//
//
//    //아래,왼쪽 제목 이름
//    private val whiteColor by lazy {
//        ContextCompat.getColor(this.requireContext(), R.color.black)
//    }
//
//    //그래프 가로 축,선 (점선으로 변경)
//    private val transparentBlackColor by lazy {
//        ContextCompat.getColor(this.requireContext(), R.color.black)
//    }
//
////    private val customMarkerView by lazy {
////        CustomMarketView(this.requireContext(), R.layout.item_marker_view)
////    }
//
//    // 현재 날짜/시간 가져오기
//    val dateNow: LocalDateTime = LocalDateTime.now(     )
//    //     LocalDate 문자열로 포맷
//    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E")
////    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
//
//
//
//    private val listData by lazy {
//        mutableListOf(
//            ChartData("월", (24.1f)),
//            ChartData("화", (9.5f)),
//            ChartData("수", (3.5f)),
//            ChartData("목", (7.5f)),
//            ChartData("금", (5.5f)),
//            ChartData("토", (12.5f)),
//            ChartData("일", (19.5f))
//        )
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentWeekBinding.inflate(inflater,container,false)
//        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_week,container,false)
//        binding.week = this
//
////        val view = inflater.inflate(R.layout.fragment_week, container, false)
//
//        //바 차트
////        chart = view.findViewById(R.id.week_bar_chart)
//        binding.weekBarChart.setNoDataText("")
//        initChart(binding.weekBarChart)
//
//        FirebaseFirestore.getInstance()
//            .collection("subject")
//            .whereEqualTo("uid", LoginUtils.getUid()!!)
//            .get() //값이 변경 시 바로 값이 변경된다.
//            .addOnSuccessListener { docs ->
//                if(docs != null) {
//                    lateinit var subjectValue: ReadSubejct
//                    docs.documents.forEach {
//                        subjects = it.toObject(Subjects::class.java)!!
//                        list.add(subjects)
////                        Log.d("qqMutableList;Subjects", list.toString())
////                        Log.d("qqMutableList;Subjects",list.get(0).timestamp.toString())
////                        Log.d("qqMutableList;Subjects", list.size.toString())
////                        subjects: Subjects
////                        private var list: MutableList<Subjects> = mutableListOf()
//                        weeklySubjectPieChart(binding.weeklyPieChart,list)
//
////                        val subject = it.toObjects(Subject::class.java)
////                        var studytimetodaylist = mutableListOf<Int>()
////                        for (i in 0..subject.size - 1) {
////                            studytimetodaylist.add(subject[i].studytime)
////                        }
////                        todayStudyTime.value = studytimetodaylist.sum()
////                        FirebaseFirestore.getInstance().collection("users").document(getUid()!!).update("todaystudytime",todayStudyTime.value)
//                    }
//                }
//            }
//
//        //차트가 7개만 보이게 세
////        chart.setVisibleXRangeMaximum(7f)
////        chart.setVisibleXRangeMinimum(7f)팅//스크롤 중지??
//        //현재 창의 왼쪽을 지정된 x 축 위치로 이동합니다.
////        chart.moveViewToX(-10f)
////        chart.moveViewTo(100f,0f, YAxis.AxisDependency.LEFT)
////        val shareButton: Button = view.findViewById(R.id.share_button)
//        binding.shareButton.setOnClickListener {
//            requestPermission()
//        }
//
//        binding.recyclerViewWeek.apply {
//            layoutManager = LinearLayoutManager(
//                requireContext(),
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//            adapter = dailySubjectAdapter
//        }
//
//        moveCalendarByWeek(binding.calendarMonday,binding.calendarSunday,binding.calRightBtn,binding.calLeftBtn)
//        return binding.root
//    }
//
//    //adapter에 데이터 추가
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        viewModel.subjectList.observe(viewLifecycleOwner) {
//            dailySubjectAdapter.setData(it)
//        }
//    }
//
//    private fun initChart(chart: BarChart) {
////        customMarkerView.chartView = chart
//        with(chart) {
////            marker = customMarkerView
//            description.isEnabled = false
//            legend.isEnabled = false
//            isDoubleTapToZoomEnabled = false
//
//            setPinchZoom(false)
//            setDrawBarShadow(false)
//            setDrawValueAboveBar(false)
//            //둥근 모서리 색상
//            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
////                setRadius(20)
//            }
//            renderer = barChartRender
//        }
//        setData(listData)
//    }
//
//    private fun setData(barData: List<ChartData>) {
//
//        val values = mutableListOf<BarEntry>()
//
//        barData.forEachIndexed { index, chartData ->
//            //첫번째 인자 x , 두번째 인자 y
//            values.add(BarEntry(index.toFloat(), chartData.value))
//        }
//
//        //막대 그래프 색상 추가
//        val barDataSet = BarDataSet(values, "").apply {
//            setDrawValues(false)
//
//            val colors = ArrayList<Int>()
//            colors.add(Color.argb(100,51, 155, 255))
//            setColors(colors)
//            highLightAlpha = 0
//        }
//
//        //막대 그래프 너비 설정
//        val dataSets = mutableListOf(barDataSet)
//        val data = BarData(dataSets as List<IBarDataSet>?).apply {
////            setValueTextSize(30F)
//            barWidth = 0.3F
//        }
//        //애니메이션 효과 0.1초
//        with(binding.weekBarChart) {
//            animateY(100)
//            xAxis.apply {
//                position = XAxis.XAxisPosition.BOTTOM
//                setDrawGridLines(false) //축 점선
//                textColor = whiteColor
//                //월 ~ 일
//                valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return barData[value.toInt()].date
//                    }
//                }
//            }
//
////            axisLeft.labelPosition = true
////            axisLeft.labelCount =
////            axisLeft.setPosition(floatArrayOf(0f, 10f, 20f, 50f, 100f, 300f))
//            //차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
//            axisRight.apply {
//                textColor = whiteColor
//                setDrawAxisLine(false) //격자
//                gridColor = transparentBlackColor
//                gridLineWidth = 0.5F
//                enableGridDashedLine(5f,5f,5f)
//
////                axisMaximum = 24F //최대값
////                granularity  = 6F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
////                setLabelCount(6,true) //축 고정간격
////                axisMinimum = 3F //최소값
//                //y축 제목 커스텀
//                valueFormatter = object : ValueFormatter() {
//                    private val mFormat: DecimalFormat = DecimalFormat("###")
//                    override fun getFormattedValue(value: Float): String {
//                        return mFormat.format(value) + "시"
//                    }
//                }
//            }
//
//            //차트 오른쪽 축, Y방향 false처리
//            axisLeft.apply {
//                isEnabled = false
//                gridColor = transparentBlackColor
//                //차트데이터 값에서 가장 큰 값
////                var chartDataMax = listData.maxBy { it -> it.value}
////                barData.forEachIndexed { index, chartData ->
////                    for (i in chartData.value) {
//////                        var chartDataMax = listData.maxBy { it -> it. }
////                        var maxValue = i
////                        Log.d("aaa", "$maxValue")
////                        while (i > axisMaximum) {
////                            count++
////                            if (i > axisMaximum) {
////                                axisMaximum = maxValue
////                            } else {
////                                axisMaximum = 9F
////                            }
////                        }
////                    }
////                }
//                setLabelCount(6,true) //축 고정간격
//                axisMaximum = 24F //최대값
//                granularity  = 6F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
//                axisMinimum = 3F //최소값
////
//
//            }
//            notifyDataSetChanged()
//            this.data = data
//            invalidate()
//        }
//    }
//
//
//    private fun moveCalendarByWeek(monDay:TextView,sunDay:TextView,rBtn:ImageButton,lBtn:ImageButton){
//        val calendarWeekNow : LocalDateTime = LocalDateTime.now()
//        val monday : LocalDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY)//해당 주차의 월요일
//        val sunday : LocalDateTime = LocalDateTime.now().with(DayOfWeek.SUNDAY)
//
//        val textformatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
//        var count: Int = 0
//
//        monDay.text = monday.format(textformatter)
//        sunDay.text = sunday.format(textformatter)
//
//        rBtn.setOnClickListener{
//            count++
//            val mondayValue : LocalDateTime = monday.plusWeeks(count.toLong())
//            monDay.text = mondayValue.format(textformatter).toString()
//            val sundayValue : LocalDateTime = sunday.plusWeeks(count.toLong())
//            sunDay.text = sundayValue.format(textformatter).toString()
//        }
//
//        lBtn.setOnClickListener{
//            count--
//            val sundayValue : LocalDateTime = sunday.plusWeeks(count.toLong())
//            sunDay.text = sundayValue.format(textformatter).toString()
//            val mondayValue : LocalDateTime = monday.plusWeeks(count.toLong())
//            monDay.text = mondayValue.format(textformatter).toString()
//        }
//
//    }
//
//    private fun weeklySubjectPieChart(pieChart : PieChart, list: MutableList<Subjects>){
//        pieChart.setUsePercentValues(true)
//        Log.d("qwqwqwqwqw",subjects.studytime.toString())
//        Log.d("qwqwqwqwqw",subjects.color.toString())
//        Log.d("aqaqAllList", list.size.toString())
//
//        val values = mutableListOf<PieEntry>()
//        val colorItems = mutableListOf<Int>()
//        list.forEachIndexed{ index, _ ->
//            values.add(PieEntry(list[index].studytime.toFloat(), list[index].name))
//            colorItems.add(index,Color.parseColor(list[index].color))
//        }
//
//        val pieDataSet = PieDataSet(values,"")
//        pieDataSet.colors = colorItems
//        pieDataSet.apply {
////            valueTextColor = Color.BLACK
//            setDrawValues(false) //차트에 표시되는 값 지우기
//            valueTextSize = 16f
//        }
//        //% : 퍼센트 수치 색상과 사이즈 지정
//        val pieData = PieData(pieDataSet)
//        pieChart.apply {
//            data = pieData
//            description.isEnabled = false //해당 그래프 오른쪽 아래 그래프의 이름을 표시한다.
//            isRotationEnabled = false //그래프를 회전판처럼 돌릴 수 있다
////            centerText = "this is color" //그래프 한 가운데 들어갈 텍스트
////            setEntryLabelColor(Color.RED) //그래프 아이템의 이름의 색 지정
//            isEnabled = false
//            legend.isEnabled = false //범례 지우기
//            isDrawHoleEnabled = true //중앙의 흰색 테두리 제거
//            holeRadius = 50f //흰색을 증앙에 꽉 채우기
//            setDrawEntryLabels(false) //차트에 있는 이름 지우
//            animateY(1400, Easing.EaseInOutQuad)
//            animate()
//        }
//
//    }
//
//    private fun requestPermission(): Boolean {
//        var permissions = false
//        TedPermission.with(context)
//            .setPermissionListener(object : PermissionListener {
//                override fun onPermissionGranted() {
//                    permissions = true      //p0=response(응답)
//                    val shareIntent = Intent(context, ShareActivity::class.java)
//                    startActivity(shareIntent)
////                    finish()
//                }
//
//                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
//                    permissions = false
//                }
//
//            })
//            .setDeniedMessage("앱을 실행하려면 권한을 허가하셔야합니다.")
//            .setPermissions(Manifest.permission.CAMERA)
//            .check()
//        return permissions
//    }
//
//
//}