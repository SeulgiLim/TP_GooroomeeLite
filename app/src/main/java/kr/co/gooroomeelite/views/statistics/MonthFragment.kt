package kr.co.gooroomeelite.views.statistics

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.gooroomeelite.R
//import kr.co.gooroomeelite.adapter.DailySubjectAdapter
import kr.co.gooroomeelite.databinding.FragmentMonthBinding
import kr.co.gooroomeelite.entity.ReadSubejct
import kr.co.gooroomeelite.entity.Subjects
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.viewmodel.SubjectViewModel
import kr.co.gooroomeelite.views.statistics.share.ShareActivity
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private val viewModel: SubjectViewModel by viewModels()

    private lateinit var chart: BarChart
//    private val dailySubjectAdapter: DailySubjectAdapter by lazy { DailySubjectAdapter(emptyList()) }


    //db값 저장
    private lateinit var subjects: Subjects
    private var list: MutableList<Subjects> = mutableListOf()


//     LocalDate 문자열로 포맷
//    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d")


    private val listData by lazy {
        mutableListOf(
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(5.5f)),
            ChartDatas("", arrayListOf(7.5f)),
            ChartDatas("", arrayListOf(8.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(10.5f)),
            ChartDatas("", arrayListOf(18.5f)),
            ChartDatas("", arrayListOf(5.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(1.5f)),
            ChartDatas("", arrayListOf(6.5f)),
            ChartDatas("", arrayListOf(19.5f)),
            ChartDatas("", arrayListOf(5.5f)),
            ChartDatas("", arrayListOf(9.5f)),
            ChartDatas("", arrayListOf(3.5f)),
            ChartDatas("", arrayListOf(16.5f)),
            ChartDatas("", arrayListOf(3.5f)),
            ChartDatas("", arrayListOf(12.5f)),
            ChartDatas("", arrayListOf(14.5f)),
            ChartDatas("", arrayListOf(15.5f)),
            ChartDatas("", arrayListOf(16.5f)),
            ChartDatas("", arrayListOf(17.5f)),
            ChartDatas("", arrayListOf(18.5f)),
            ChartDatas("", arrayListOf(12.5f))
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
//        val view = inflater.inflate(R.layout.fragment_month, container, false)

//        val shareButton: Button = view.findViewById(R.id.share_button)/
        binding.shareButton.setOnClickListener {
            requestPermission()
        }
        //바 차트
//        chart = view.findViewById(R.id.month_bar_chart)
        binding.monthBarChart.setNoDataText("")
        initChart(binding.monthBarChart)
        binding.monthBarChart.setVisibleXRangeMaximum(30f)
        binding.monthBarChart.moveViewToX(30f)

        FirebaseFirestore.getInstance()
            .collection("subject")
            .whereEqualTo("uid", LoginUtils.getUid()!!)
            .get() //값이 변경 시 바로 값이 변경된다.
            .addOnSuccessListener { docs ->
                if(docs != null) {
                    lateinit var subjectValue: ReadSubejct
                    docs.documents.forEach {
                        subjectValue = ReadSubejct(it.toObject(Subjects::class.java)!!,it)
                        subjects = it.toObject(Subjects::class.java)!!
                        list.add(subjects)
                        monthlySubjectPieChart(binding.weeklyPieChart,list)
                        Log.d("aqaqList", list.size.toString())
                        //일간 공부 시간
                    }
                }
            }

//        var calendarMonth : TextView = view.findViewById(R.id.calendar_month)
//        var calRightBtn : ImageButton = view.findViewById(R.id.cal_right_btn)
//        var calLeftBtn : ImageButton = view.findViewById(R.id.cal_left_btn)
//        binding.recyclerViewMonth.apply {
//            layoutManager = LinearLayoutManager(
//                requireContext(),
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//            adapter = dailySubjectAdapter
//        }

        moveCalendarByDay(binding.calendarMonth,binding.calRightBtn,binding.calLeftBtn,binding.titleMonth)
        return binding.root
    }

    private fun moveCalendarByDay(calendarMonth:TextView,calRightBtn:ImageButton,calLeftBtn:ImageButton,title:TextView){
        // 현재 날짜/시간 가져오기
        val dateNow: LocalDate = LocalDate.now()
        val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM")

        var count : Int = 0
        calendarMonth.text = dateNow.format(textformatter) //하루 2021.07.08

        dateNow.plusDays(count.toLong()) //일간탭으로 돌아왔을 때 오늘 날짜로 다시 변경
        calRightBtn.setOnClickListener {
            count++
            val dayPlus: LocalDate = dateNow.plusMonths(count.toLong())
            calendarMonth.text =  dayPlus.format(textformatter).toString()
        }

        calLeftBtn.setOnClickListener {
            count--
            val minusDay: LocalDate = dateNow.plusMonths(count.toLong())
            calendarMonth.text =  minusDay.format(textformatter).toString()
        }
    }

//    //adapter에 데이터 추가
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        viewModel.subjectList.observe(viewLifecycleOwner) {
//            dailySubjectAdapter.setData(it)
//        }
//    }

    private fun monthlySubjectPieChart(pieChart : PieChart, list: MutableList<Subjects>){
        pieChart.setUsePercentValues(true)
//        Log.d("qwqwqwqwqw",subjects.studytime.toString())
//        Log.d("qwqwqwqwqw",subjects.color.toString())
        Log.d("aqaqAllList", list.size.toString())

        val values = mutableListOf<PieEntry>()
        val colorItems = mutableListOf<Int>()
        list.forEachIndexed{ index, _ ->
            values.add(PieEntry(list[index].studytime.toFloat(), list[index].name))
            colorItems.add(index,Color.parseColor(list[index].color))
        }

        val pieDataSet = PieDataSet(values,"")
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
//                setRadius(10)
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
            barWidth = 0.5F
        }
        //애니메이션 효과 0.1초
        with(binding.monthBarChart) {
            animateY(100)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
//                setVisibleXRangeMaximum(20f)    //최대 X좌표 기준으로 몇개의 데이터를 보여줄지 설정함
//                moveViewToX(60f)
//                setVisibleXRangeMaximum(100f)
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

            notifyDataSetChanged()  //chart의 값 변동을 감지함
//            setVisibleXRangeMaximum(10f)    //최대 X좌표 기준으로 몇개의 데이터를 보여줄지 설정함
//            moveViewToX(10f)
//            setVisibleXRangeMaximum(10f)

            this.data = data
            invalidate()
        }
    }
}
//    private fun listDataValue(){
//        var i : Int = 1
//        while (i <= 31) {
//            val name : String = ""
//            mutableListOf(
//
//                ChartDatas(name,(Math.random()*16).toFloat())
//            )
//            i++
//            return
//        }
//    }