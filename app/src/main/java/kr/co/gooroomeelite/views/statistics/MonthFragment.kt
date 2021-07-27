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

    var count : Int = 0
    var monthCount : Int = 0

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
        moveCalendarByMonth()
        return binding.root
    }

    private fun divideDataFromFirebase() {
        viewModel.list.observe(viewLifecycleOwner) {
            val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            //오늘
            val dateNow: LocalDateTime = LocalDateTime.now()
            val dateNowFormat : String = dateNow.format(textformatter)
            Log.d("dafgsdfh",dateNowFormat)
            //해당 월의 마지막 날짜 찾기
            val targetYearMonth: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormat, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            val end = targetYearMonth.atEndOfMonth()
            Log.d("dafgsdfh",end.toString())

            //5주 일주일 간 데이터
            val monDay5 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay5 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay5 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay5 = end.with(DayOfWeek.THURSDAY) //목
            val friDay5 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay5 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay5 = end.with(DayOfWeek.SUNDAY) //일

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

            //4주 일주일 간 데이터
            val monDay4 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay4 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay4 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay4 = end.with(DayOfWeek.THURSDAY) //목
            val friDay4 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay4 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay4 = end.with(DayOfWeek.SUNDAY) //일

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

            //3주 일주일 간 데이터
            val monDay3 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay3 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay3 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay3 = end.with(DayOfWeek.THURSDAY) //목
            val friDay3 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay3 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay3 = end.with(DayOfWeek.SUNDAY) //일

            var mondays3Value : Float = 0f
            var tuesdays3Value: Float = 0f
            var wednesdays3Value: Float = 0f
            var thursdays3Value: Float = 0f
            var fridays3Value: Float = 0f
            var saturdays3Value: Float = 0f
            var sundays3Value: Float = 0f

            var mondaySum3: Float = 0f
            var tuesdaySum3: Float = 0f
            var wednesdaySum3: Float = 0f
            var thursdaySum3: Float = 0f
            var fridaySum3: Float = 0f
            var saturdaySum3: Float = 0f
            var sundaySum3: Float = 0f
            var totalSum3: Float = 0f

            //2주 일주일 간 데이터
            val monDay2 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay2 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay2 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay2 = end.with(DayOfWeek.THURSDAY) //목
            val friDay2 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay2 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay2 = end.with(DayOfWeek.SUNDAY) //일

            var mondays2Value : Float = 0f
            var tuesdays2Value: Float = 0f
            var wednesdays2Value: Float = 0f
            var thursdays2Value: Float = 0f
            var fridays2Value: Float = 0f
            var saturdays2Value: Float = 0f
            var sundays2Value: Float = 0f

            var mondaySum2: Float = 0f
            var tuesdaySum2: Float = 0f
            var wednesdaySum2: Float = 0f
            var thursdaySum2: Float = 0f
            var fridaySum2: Float = 0f
            var saturdaySum2: Float = 0f
            var sundaySum2: Float = 0f
            var totalSum2: Float = 0f

            //1주 일주일 간 데이터
            val monDay1 = end.with(DayOfWeek.MONDAY) //월
            val tuesDay1 = end.with(DayOfWeek.TUESDAY) //화
            val wednesDay1 = end.with(DayOfWeek.WEDNESDAY) //수
            val thursDay1 = end.with(DayOfWeek.THURSDAY) //목
            val friDay1 = end.with(DayOfWeek.FRIDAY) //금
            val saturDay1 = end.with(DayOfWeek.SATURDAY) //토
            val sunDay1 = end.with(DayOfWeek.SUNDAY) //일
            Log.d("totalSumMay", sunDay1.minusWeeks(4).format(textformatter).toString() ) //25

            var mondays1Value : Float = 0f
            var tuesdays1Value: Float = 0f
            var wednesdays1Value: Float = 0f
            var thursdays1Value: Float = 0f
            var fridays1Value: Float = 0f
            var saturdays1Value: Float = 0f
            var sundays1Value: Float = 0f

            var mondaySum1: Float = 0f
            var tuesdaySum1: Float = 0f
            var wednesdaySum1: Float = 0f
            var thursdaySum1: Float = 0f
            var fridaySum1: Float = 0f
            var saturdaySum1: Float = 0f
            var sundaySum1: Float = 0f
            var totalSum1: Float = 0f

            var totalTime : Float = 0f

            //7월
            val cal = Calendar.getInstance()
            cal.get(Calendar.WEEK_OF_MONTH)
            Log.d("asdfadsf",cal.get(Calendar.WEEK_OF_MONTH).toString())//캘린더를 이용한 마지막 날 주 구하기

            val sizeOfWeek: Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
            Log.d("asdfadsf",sizeOfWeek.toString()) //마지막날의 주 사이즈 구하기 !!!!!~~~~!!!!!~~~~~~!!!!

            //6월
//            val dateNowBefore: LocalDateTime = LocalDateTime.now().minusMonths(1.toLong())
//            val dateNowFormatBefore : String = dateNowBefore.format(textformatter)
//            Log.d("dafgsdfhBefore",dateNowFormatBefore)//6월 마지막
//            //해당 월의 마지막 날짜 찾기
//            val targetYearMonths: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormatBefore, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//            val ends = targetYearMonths.atEndOfMonth()
//            Log.d("dafgsdfhBefore",ends.toString())
//            val endOfWeek : Int = ends.get(ChronoField.ALIGNED_WEEK_OF_MONTH)
//            Log.d("dafgsdfhBefore6week",endOfWeek.toString())
//
//            //5월
//            val dateNowBefore5: LocalDateTime = LocalDateTime.now().minusMonths(2.toLong())
//            val dateNowFormatBefore5 : String = dateNowBefore5.format(textformatter)
//            Log.d("dafgsdfhBefore5",dateNowFormatBefore5.toString())//6월 마지막
//            //해당 월의 마지막 날짜 찾기
//            val targetYearMonths5: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormatBefore5, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//            val ends5 = targetYearMonths5.atEndOfMonth()
//            Log.d("dafgsdfhBefore5",ends5.toString())
//            val endOfWeek5 : Int = ends5.get(ChronoField.ALIGNED_WEEK_OF_MONTH)
//            Log.d("dafgsdfhBeforeweek5",endOfWeek5.toString())
//
//            //4월
//            val dateNowBefore4: LocalDateTime = LocalDateTime.now().minusMonths(3.toLong())
//            val dateNowFormatBefore4 : String = dateNowBefore4.format(textformatter)
//            Log.d("dafgsdfhBefore4",dateNowFormatBefore4)//6월 마지막
//            //해당 월의 마지막 날짜 찾기
//            val targetYearMonths4: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormatBefore4, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//            val ends4 = targetYearMonths4.atEndOfMonth()
//            Log.d("dafgsdfhBefore4",ends4.toString())
//            val endOfWeek4 : Int = ends4.get(ChronoField.ALIGNED_WEEK_OF_MONTH)
//            Log.d("dafgsdfhBeforeweek4",endOfWeek4.toString())
//            val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
//            val dateNow: String? = LocalDateTime.now().format(textformatter) //오늘
//            val targetYearMonth: YearMonth = YearMonth.from(LocalDate.parse(dateNow.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//            val end = targetYearMonth.atEndOfMonth() //한달의 마지막 날
//            val endOfWeek: Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
//            Log.d("asdfadsf",weekOfMonth.toString()) //마지막날의 주 사이즈 구하기

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
                    }else if(monDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        mondays3Value = subject.studytimeCopy.toFloat()
                        mondaySum3 = mondaySum3 + mondays3Value
                        break
                    }else if(monDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        mondays2Value = subject.studytimeCopy.toFloat()
                        mondaySum2 = mondaySum2 + mondays2Value
                        break
                    }else if(monDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        mondays1Value = subject.studytimeCopy.toFloat()
                        mondaySum1 = mondaySum1 + mondays1Value
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
                    }else if(tuesDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        tuesdays3Value = subject.studytimeCopy.toFloat()
                        tuesdaySum3 = tuesdaySum3 + tuesdays3Value
                        break
                    }else if(tuesDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        tuesdays2Value = subject.studytimeCopy.toFloat()
                        tuesdaySum2 = tuesdaySum2 + tuesdays2Value
                        break
                    }else if(tuesDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        tuesdays1Value = subject.studytimeCopy.toFloat()
                        tuesdaySum1 = tuesdaySum1 + tuesdays1Value
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
                    }else if(wednesDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        wednesdays3Value = subject.studytimeCopy.toFloat()
                        wednesdaySum3 = wednesdaySum3 + wednesdays3Value
                        break
                    }else if(wednesDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        wednesdays2Value = subject.studytimeCopy.toFloat()
                        wednesdaySum2 = wednesdaySum2 + wednesdays2Value
                        break
                    }else if(wednesDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        wednesdays1Value = subject.studytimeCopy.toFloat()
                        wednesdaySum1 = wednesdaySum1 + wednesdays1Value
                        break
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
                    }else if(thursDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        thursdays3Value = subject.studytimeCopy.toFloat()
                        thursdaySum3 = thursdaySum3 + thursdays3Value
                        break
                    }else if(thursDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        thursdays2Value = subject.studytimeCopy.toFloat()
                        thursdaySum2 = thursdaySum2 + thursdays2Value
                        break
                    }else if(thursDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        thursdays1Value = subject.studytimeCopy.toFloat()
                        thursdaySum1 = thursdaySum1 + thursdays1Value
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
                    }else if(friDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        fridays3Value = subject.studytimeCopy.toFloat()
                        fridaySum3 = fridaySum3 + fridays3Value
                        break
                    }else if(friDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        fridays2Value = subject.studytimeCopy.toFloat()
                        fridaySum2 = fridaySum2 + fridays2Value
                        break
                    }else if(friDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        fridays1Value = subject.studytimeCopy.toFloat()
                        fridaySum1 = fridaySum1 + fridays1Value
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
                    }else if(saturDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        saturdays3Value = subject.studytimeCopy.toFloat()
                        saturdaySum3 = saturdaySum3 + saturdays3Value
                        break
                    }else if(saturDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        saturdays2Value = subject.studytimeCopy.toFloat()
                        saturdaySum2 = saturdaySum2 + saturdays2Value
                        break
                    }else if(saturDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        saturdays1Value = subject.studytimeCopy.toFloat()
                        saturdaySum1 = saturdaySum1 + saturdays1Value
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
                    }else if(sunDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                        sundays3Value = subject.studytimeCopy.toFloat()
                        sundaySum3 = sundaySum3 + sundays3Value
                        break
                    }else if(sunDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                        sundays2Value = subject.studytimeCopy.toFloat()
                        sundaySum2 = sundaySum2 + sundays2Value
                        break
                    }else if(sunDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                        Log.d("totalSumMay", sunDay1.minusWeeks(4).format(textformatter).toString() ) //25
                        sundays1Value = subject.studytimeCopy.toFloat()
                        sundaySum1 = sundaySum1 + sundays1Value
                        break
                    }
                }
            }

            totalSum5 = mondaySum5 + tuesdaySum5 + wednesdaySum5 + thursdaySum5 + fridaySum5 + saturdaySum5 + sundaySum5
            totalSum4 = mondaySum4 + tuesdaySum4 + wednesdaySum4 + thursdaySum4 + fridaySum4 + saturdaySum4 + sundaySum4
            totalSum3 = mondaySum3 + tuesdaySum3 + wednesdaySum3 + thursdaySum3 + fridaySum3 + saturdaySum3 + sundaySum3
            totalSum2 = mondaySum2 + tuesdaySum2 + wednesdaySum2 + thursdaySum2 + fridaySum2 + saturdaySum2 + sundaySum2
            totalSum1 = mondaySum1 + tuesdaySum1 + wednesdaySum1 + thursdaySum1 + fridaySum1 + saturdaySum1 + sundaySum1
            totalTime = totalSum5 + totalSum4 + totalSum3 + totalSum2 + totalSum1

            binding.monthlyTotalTime.text =  "${(totalTime.toInt()) / 60}시간 ${(totalTime.toInt()) % 60}분"
            Log.d("totalSum 7.26", (totalSum5/60).toString() + " 총합") //25
            Log.d("totalSum 7.19", (totalSum4/60).toString() + " 총합") //25
            Log.d("totalSum 7.12", (totalSum3/60).toString() + " 총합") //25
            Log.d("totalSum 7.5", (totalSum2/60).toString() + " 총합") //25
            Log.d("totalSum 7.4~~", (totalSum1/60).toString() + " 총합") //25
            Log.d("totalSum sizeOfWeek", sizeOfWeek.toString() + " 총합") //25
            setData(totalSum1, totalSum2, totalSum3, totalSum4, totalSum5)
        }
    }

    private fun moveCalendarByMonth(){
//        calendarMonth:TextView, calRightBtn:ImageButton, calLeftBtn:ImageButton, title:TextView
//        binding.calendarMonth, binding.calRightBtn, binding.calLeftBtn, binding.titleMonth
        // 현재 날짜/시간 가져오기
        val dateNow: LocalDate = LocalDate.now()
        val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM")
        val titleformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월달에")

        var count : Int = 0
        var monthCount : Int = 0
        binding.calendarMonth.text = dateNow.format(textformatter) //하루 2021.07.08

        dateNow.plusDays(count.toLong()) //일간탭으로 돌아왔을 때 오늘 날짜로 다시 변경
        binding.calRightBtn.setOnClickListener {
            count++
            if(count == 1){
                binding.calRightBtn.isEnabled = false
            }else {
                val dayPlus: LocalDate = dateNow.plusMonths(count.toLong())
                binding.calendarMonth.text = dayPlus.format(textformatter).toString()
                if (count == 0) {
                    binding.titleMonth.text = "이번 달에"
                } else if (count == -1) {
                    binding.calRightBtn.isEnabled = true
                    binding.titleMonth.text = "지난 달에"
                } else {
                    binding.titleMonth.text = dayPlus.format(titleformatter).toString()
                }
            }
            monthCount++
            //----------------------------------- monthCount++
            viewModel.list.observe(viewLifecycleOwner) {
                val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                //오늘
                val dateNow: LocalDateTime = LocalDateTime.now()//------------------monthCount
                val dateNowFormat : String = dateNow.format(textformatter)
                Log.d("dafgsdfh",dateNowFormat)
                //해당 월의 마지막 날짜 찾기
                val targetYearMonth: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormat, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                val end = targetYearMonth.atEndOfMonth().plusMonths(monthCount.toLong())////------------------monthCount
                Log.d("dafgsdfh",end.toString())
                //마지막날의 주 사이즈 구하기
                val sizeOfWeek:Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
                Log.d("asdfadsfSize",sizeOfWeek.toString())
//                val weekOfMonthSize : Int = weekSize-1
//                Log.d("asdfadsfSize-Minus",weekOfMonthSize.toString())


                //5주 일주일 간 데이터
                val monDay5 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay5 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay5 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay5 = end.with(DayOfWeek.THURSDAY) //목
                val friDay5 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay5 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay5 = end.with(DayOfWeek.SUNDAY) //일

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

                //4주 일주일 간 데이터
                val monDay4 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay4 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay4 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay4 = end.with(DayOfWeek.THURSDAY) //목
                val friDay4 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay4 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay4 = end.with(DayOfWeek.SUNDAY) //일

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

                //3주 일주일 간 데이터
                val monDay3 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay3 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay3 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay3 = end.with(DayOfWeek.THURSDAY) //목
                val friDay3 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay3 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay3 = end.with(DayOfWeek.SUNDAY) //일

                var mondays3Value : Float = 0f
                var tuesdays3Value: Float = 0f
                var wednesdays3Value: Float = 0f
                var thursdays3Value: Float = 0f
                var fridays3Value: Float = 0f
                var saturdays3Value: Float = 0f
                var sundays3Value: Float = 0f

                var mondaySum3: Float = 0f
                var tuesdaySum3: Float = 0f
                var wednesdaySum3: Float = 0f
                var thursdaySum3: Float = 0f
                var fridaySum3: Float = 0f
                var saturdaySum3: Float = 0f
                var sundaySum3: Float = 0f
                var totalSum3: Float = 0f

                //2주 일주일 간 데이터
                val monDay2 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay2 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay2 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay2 = end.with(DayOfWeek.THURSDAY) //목
                val friDay2 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay2 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay2 = end.with(DayOfWeek.SUNDAY) //일

                var mondays2Value : Float = 0f
                var tuesdays2Value: Float = 0f
                var wednesdays2Value: Float = 0f
                var thursdays2Value: Float = 0f
                var fridays2Value: Float = 0f
                var saturdays2Value: Float = 0f
                var sundays2Value: Float = 0f

                var mondaySum2: Float = 0f
                var tuesdaySum2: Float = 0f
                var wednesdaySum2: Float = 0f
                var thursdaySum2: Float = 0f
                var fridaySum2: Float = 0f
                var saturdaySum2: Float = 0f
                var sundaySum2: Float = 0f
                var totalSum2: Float = 0f

                //1주 일주일 간 데이터
                val monDay1 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay1 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay1 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay1 = end.with(DayOfWeek.THURSDAY) //목
                val friDay1 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay1 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay1 = end.with(DayOfWeek.SUNDAY) //일
                Log.d("totalSumMay", sunDay1.minusWeeks(4).format(textformatter).toString() ) //25

                var mondays1Value : Float = 0f
                var tuesdays1Value: Float = 0f
                var wednesdays1Value: Float = 0f
                var thursdays1Value: Float = 0f
                var fridays1Value: Float = 0f
                var saturdays1Value: Float = 0f
                var sundays1Value: Float = 0f

                var mondaySum1: Float = 0f
                var tuesdaySum1: Float = 0f
                var wednesdaySum1: Float = 0f
                var thursdaySum1: Float = 0f
                var fridaySum1: Float = 0f
                var saturdaySum1: Float = 0f
                var sundaySum1: Float = 0f
                var totalSum1: Float = 0f

                var totalTime : Float = 0f

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
                        }else if(monDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            mondays3Value = subject.studytimeCopy.toFloat()
                            mondaySum3 = mondaySum3 + mondays3Value
                            break
                        }else if(monDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            mondays2Value = subject.studytimeCopy.toFloat()
                            mondaySum2 = mondaySum2 + mondays2Value
                            break
                        }else if(monDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            mondays1Value = subject.studytimeCopy.toFloat()
                            mondaySum1 = mondaySum1 + mondays1Value
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
                        }else if(tuesDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            tuesdays3Value = subject.studytimeCopy.toFloat()
                            tuesdaySum3 = tuesdaySum3 + tuesdays3Value
                            break
                        }else if(tuesDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            tuesdays2Value = subject.studytimeCopy.toFloat()
                            tuesdaySum2 = tuesdaySum2 + tuesdays2Value
                            break
                        }else if(tuesDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            tuesdays1Value = subject.studytimeCopy.toFloat()
                            tuesdaySum1 = tuesdaySum1 + tuesdays1Value
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
                        }else if(wednesDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            wednesdays3Value = subject.studytimeCopy.toFloat()
                            wednesdaySum3 = wednesdaySum3 + wednesdays3Value
                            break
                        }else if(wednesDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            wednesdays2Value = subject.studytimeCopy.toFloat()
                            wednesdaySum2 = wednesdaySum2 + wednesdays2Value
                            break
                        }else if(wednesDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            wednesdays1Value = subject.studytimeCopy.toFloat()
                            wednesdaySum1 = wednesdaySum1 + wednesdays1Value
                            break
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
                        }else if(thursDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            thursdays3Value = subject.studytimeCopy.toFloat()
                            thursdaySum3 = thursdaySum3 + thursdays3Value
                            break
                        }else if(thursDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            thursdays2Value = subject.studytimeCopy.toFloat()
                            thursdaySum2 = thursdaySum2 + thursdays2Value
                            break
                        }else if(thursDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            thursdays1Value = subject.studytimeCopy.toFloat()
                            thursdaySum1 = thursdaySum1 + thursdays1Value
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
                        }else if(friDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            fridays3Value = subject.studytimeCopy.toFloat()
                            fridaySum3 = fridaySum3 + fridays3Value
                            break
                        }else if(friDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            fridays2Value = subject.studytimeCopy.toFloat()
                            fridaySum2 = fridaySum2 + fridays2Value
                            break
                        }else if(friDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            fridays1Value = subject.studytimeCopy.toFloat()
                            fridaySum1 = fridaySum1 + fridays1Value
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
                        }else if(saturDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            saturdays3Value = subject.studytimeCopy.toFloat()
                            saturdaySum3 = saturdaySum3 + saturdays3Value
                            break
                        }else if(saturDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            saturdays2Value = subject.studytimeCopy.toFloat()
                            saturdaySum2 = saturdaySum2 + saturdays2Value
                            break
                        }else if(saturDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            saturdays1Value = subject.studytimeCopy.toFloat()
                            saturdaySum1 = saturdaySum1 + saturdays1Value
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
                        }else if(sunDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            sundays3Value = subject.studytimeCopy.toFloat()
                            sundaySum3 = sundaySum3 + sundays3Value
                            break
                        }else if(sunDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            sundays2Value = subject.studytimeCopy.toFloat()
                            sundaySum2 = sundaySum2 + sundays2Value
                            break
                        }else if(sunDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            Log.d("totalSumMay", sunDay1.minusWeeks(4).format(textformatter).toString() ) //25
                            sundays1Value = subject.studytimeCopy.toFloat()
                            sundaySum1 = sundaySum1 + sundays1Value
                            break
                        }
                    }
                }

                totalSum5 = mondaySum5 + tuesdaySum5 + wednesdaySum5 + thursdaySum5 + fridaySum5 + saturdaySum5 + sundaySum5
                totalSum4 = mondaySum4 + tuesdaySum4 + wednesdaySum4 + thursdaySum4 + fridaySum4 + saturdaySum4 + sundaySum4
                totalSum3 = mondaySum3 + tuesdaySum3 + wednesdaySum3 + thursdaySum3 + fridaySum3 + saturdaySum3 + sundaySum3
                totalSum2 = mondaySum2 + tuesdaySum2 + wednesdaySum2 + thursdaySum2 + fridaySum2 + saturdaySum2 + sundaySum2
                totalSum1 = mondaySum1 + tuesdaySum1 + wednesdaySum1 + thursdaySum1 + fridaySum1 + saturdaySum1 + sundaySum1
                totalTime = totalSum5 + totalSum4 + totalSum3 + totalSum2 + totalSum1
                binding.monthlyTotalTime.text =  "${(totalTime.toInt()) / 60}시간 ${(totalTime.toInt()) % 60}분"
                Log.d("totalSum 7.26", (totalSum5/60).toString() + " 총합") //25
                Log.d("totalSum 7.19", (totalSum4/60).toString() + " 총합") //25
                Log.d("totalSum 7.12", (totalSum3/60).toString() + " 총합") //25
                Log.d("totalSum 7.5", (totalSum2/60).toString() + " 총합") //25
                Log.d("totalSum 7.4~~", (totalSum1/60).toString() + " 총합") //25
                setData(totalSum1, totalSum2, totalSum3, totalSum4, totalSum5)
            }
        }

        binding.calLeftBtn.setOnClickListener {
            count--
            val minusDay: LocalDate = dateNow.plusMonths(count.toLong())
            binding.calendarMonth.text =  minusDay.format(textformatter).toString()
            if (count == 0) {
                binding.titleMonth.text = "이번 달에"
            } else if (count == -1) {
                binding.titleMonth.text = "지난 달에"
                binding.calRightBtn.isEnabled = true
            } else {
                binding.titleMonth.text = minusDay.format(titleformatter).toString()
            }
            monthCount--
            //-----------------------------------  monthCount--
            viewModel.list.observe(viewLifecycleOwner) {
                val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                //오늘
                val dateNow: LocalDateTime = LocalDateTime.now()//------------------monthCount
                val dateNowFormat : String = dateNow.format(textformatter)
                Log.d("dafgsdfh",dateNowFormat)
                //해당 월의 마지막 날짜 찾기
                val targetYearMonth: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormat, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                val end = targetYearMonth.atEndOfMonth().plusMonths(monthCount.toLong())////------------------monthCount
                Log.d("dafgsdfh",end.toString())
                //마지막날의 주 사이즈 구하기
                val sizeOfWeek:Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
                Log.d("asdfadsfSize",sizeOfWeek.toString())
//                val weekOfMonthSize : Int = weekSize-1
//                Log.d("asdfadsfSize-Minus",weekOfMonthSize.toString())


                //5주 일주일 간 데이터
                val monDay5 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay5 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay5 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay5 = end.with(DayOfWeek.THURSDAY) //목
                val friDay5 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay5 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay5 = end.with(DayOfWeek.SUNDAY) //일

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

                //4주 일주일 간 데이터
                val monDay4 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay4 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay4 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay4 = end.with(DayOfWeek.THURSDAY) //목
                val friDay4 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay4 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay4 = end.with(DayOfWeek.SUNDAY) //일

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

                //3주 일주일 간 데이터
                val monDay3 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay3 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay3 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay3 = end.with(DayOfWeek.THURSDAY) //목
                val friDay3 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay3 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay3 = end.with(DayOfWeek.SUNDAY) //일

                var mondays3Value : Float = 0f
                var tuesdays3Value: Float = 0f
                var wednesdays3Value: Float = 0f
                var thursdays3Value: Float = 0f
                var fridays3Value: Float = 0f
                var saturdays3Value: Float = 0f
                var sundays3Value: Float = 0f

                var mondaySum3: Float = 0f
                var tuesdaySum3: Float = 0f
                var wednesdaySum3: Float = 0f
                var thursdaySum3: Float = 0f
                var fridaySum3: Float = 0f
                var saturdaySum3: Float = 0f
                var sundaySum3: Float = 0f
                var totalSum3: Float = 0f

                //2주 일주일 간 데이터
                val monDay2 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay2 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay2 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay2 = end.with(DayOfWeek.THURSDAY) //목
                val friDay2 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay2 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay2 = end.with(DayOfWeek.SUNDAY) //일

                var mondays2Value : Float = 0f
                var tuesdays2Value: Float = 0f
                var wednesdays2Value: Float = 0f
                var thursdays2Value: Float = 0f
                var fridays2Value: Float = 0f
                var saturdays2Value: Float = 0f
                var sundays2Value: Float = 0f

                var mondaySum2: Float = 0f
                var tuesdaySum2: Float = 0f
                var wednesdaySum2: Float = 0f
                var thursdaySum2: Float = 0f
                var fridaySum2: Float = 0f
                var saturdaySum2: Float = 0f
                var sundaySum2: Float = 0f
                var totalSum2: Float = 0f

                //1주 일주일 간 데이터
                val monDay1 = end.with(DayOfWeek.MONDAY) //월
                val tuesDay1 = end.with(DayOfWeek.TUESDAY) //화
                val wednesDay1 = end.with(DayOfWeek.WEDNESDAY) //수
                val thursDay1 = end.with(DayOfWeek.THURSDAY) //목
                val friDay1 = end.with(DayOfWeek.FRIDAY) //금
                val saturDay1 = end.with(DayOfWeek.SATURDAY) //토
                val sunDay1 = end.with(DayOfWeek.SUNDAY) //일
                Log.d("totalSumMay", sunDay1.minusWeeks(4).format(textformatter).toString() ) //25

                var mondays1Value : Float = 0f
                var tuesdays1Value: Float = 0f
                var wednesdays1Value: Float = 0f
                var thursdays1Value: Float = 0f
                var fridays1Value: Float = 0f
                var saturdays1Value: Float = 0f
                var sundays1Value: Float = 0f

                var mondaySum1: Float = 0f
                var tuesdaySum1: Float = 0f
                var wednesdaySum1: Float = 0f
                var thursdaySum1: Float = 0f
                var fridaySum1: Float = 0f
                var saturdaySum1: Float = 0f
                var sundaySum1: Float = 0f
                var totalSum1: Float = 0f

                var totalTime : Float = 0f

                val cal = Calendar.getInstance()
                cal.get(Calendar.WEEK_OF_MONTH)
                Log.d("asdfadsf",cal.get(Calendar.WEEK_OF_MONTH).toString())//캘린더를 이용한 마지막 날 주 구하기

                val weekOfMonth: Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
                Log.d("asdfadsf",weekOfMonth.toString()) //마지막날의 주 사이즈 구하기

//                //한달 전
//                val dateNowBefore: LocalDateTime = LocalDateTime.now().minusMonths(1.toLong())
//                val dateNowFormatBefore : String = dateNowBefore.format(textformatter)
//                Log.d("dafgsdfhBefore",dateNowFormatBefore)//6월 마지막
//                //해당 월의 마지막 날짜 찾기
//                val targetYearMonths: YearMonth = YearMonth.from(LocalDate.parse(dateNowFormatBefore, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//                val ends = targetYearMonths.atEndOfMonth()
//                Log.d("dafgsdfhBefore",ends.toString())
//                val endOfWeek : Int = ends.get(ChronoField.ALIGNED_WEEK_OF_MONTH)
//                Log.d("dafgsdfhBefore6week",endOfWeek.toString())

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
                        }else if(monDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            mondays3Value = subject.studytimeCopy.toFloat()
                            mondaySum3 = mondaySum3 + mondays3Value
                            break
                        }else if(monDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            mondays2Value = subject.studytimeCopy.toFloat()
                            mondaySum2 = mondaySum2 + mondays2Value
                            break
                        }else if(monDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            mondays1Value = subject.studytimeCopy.toFloat()
                            mondaySum1 = mondaySum1 + mondays1Value
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
                        }else if(tuesDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            tuesdays3Value = subject.studytimeCopy.toFloat()
                            tuesdaySum3 = tuesdaySum3 + tuesdays3Value
                            break
                        }else if(tuesDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            tuesdays2Value = subject.studytimeCopy.toFloat()
                            tuesdaySum2 = tuesdaySum2 + tuesdays2Value
                            break
                        }else if(tuesDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            tuesdays1Value = subject.studytimeCopy.toFloat()
                            tuesdaySum1 = tuesdaySum1 + tuesdays1Value
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
                        }else if(wednesDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            wednesdays3Value = subject.studytimeCopy.toFloat()
                            wednesdaySum3 = wednesdaySum3 + wednesdays3Value
                            break
                        }else if(wednesDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            wednesdays2Value = subject.studytimeCopy.toFloat()
                            wednesdaySum2 = wednesdaySum2 + wednesdays2Value
                            break
                        }else if(wednesDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            wednesdays1Value = subject.studytimeCopy.toFloat()
                            wednesdaySum1 = wednesdaySum1 + wednesdays1Value
                            break
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
                        }else if(thursDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            thursdays3Value = subject.studytimeCopy.toFloat()
                            thursdaySum3 = thursdaySum3 + thursdays3Value
                            break
                        }else if(thursDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            thursdays2Value = subject.studytimeCopy.toFloat()
                            thursdaySum2 = thursdaySum2 + thursdays2Value
                            break
                        }else if(thursDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            thursdays1Value = subject.studytimeCopy.toFloat()
                            thursdaySum1 = thursdaySum1 + thursdays1Value
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
                        }else if(friDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            fridays3Value = subject.studytimeCopy.toFloat()
                            fridaySum3 = fridaySum3 + fridays3Value
                            break
                        }else if(friDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            fridays2Value = subject.studytimeCopy.toFloat()
                            fridaySum2 = fridaySum2 + fridays2Value
                            break
                        }else if(friDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            fridays1Value = subject.studytimeCopy.toFloat()
                            fridaySum1 = fridaySum1 + fridays1Value
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
                        }else if(saturDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            saturdays3Value = subject.studytimeCopy.toFloat()
                            saturdaySum3 = saturdaySum3 + saturdays3Value
                            break
                        }else if(saturDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            saturdays2Value = subject.studytimeCopy.toFloat()
                            saturdaySum2 = saturdaySum2 + saturdays2Value
                            break
                        }else if(saturDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            saturdays1Value = subject.studytimeCopy.toFloat()
                            saturdaySum1 = saturdaySum1 + saturdays1Value
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
                        }else if(sunDay3.minusWeeks(2).format(textformatter) == serverDateFormat){
                            sundays3Value = subject.studytimeCopy.toFloat()
                            sundaySum3 = sundaySum3 + sundays3Value
                            break
                        }else if(sunDay2.minusWeeks(3).format(textformatter) == serverDateFormat){
                            sundays2Value = subject.studytimeCopy.toFloat()
                            sundaySum2 = sundaySum2 + sundays2Value
                            break
                        }else if(sunDay1.minusWeeks(4).format(textformatter) == serverDateFormat){
                            Log.d("totalSumMay", sunDay1.minusWeeks(4).format(textformatter).toString() ) //25
                            sundays1Value = subject.studytimeCopy.toFloat()
                            sundaySum1 = sundaySum1 + sundays1Value
                            break
                        }
                    }
                }

                totalSum5 = mondaySum5 + tuesdaySum5 + wednesdaySum5 + thursdaySum5 + fridaySum5 + saturdaySum5 + sundaySum5
                totalSum4 = mondaySum4 + tuesdaySum4 + wednesdaySum4 + thursdaySum4 + fridaySum4 + saturdaySum4 + sundaySum4
                totalSum3 = mondaySum3 + tuesdaySum3 + wednesdaySum3 + thursdaySum3 + fridaySum3 + saturdaySum3 + sundaySum3
                totalSum2 = mondaySum2 + tuesdaySum2 + wednesdaySum2 + thursdaySum2 + fridaySum2 + saturdaySum2 + sundaySum2
                totalSum1 = mondaySum1 + tuesdaySum1 + wednesdaySum1 + thursdaySum1 + fridaySum1 + saturdaySum1 + sundaySum1
                totalTime = totalSum5 + totalSum4 + totalSum3 + totalSum2 + totalSum1
                binding.monthlyTotalTime.text =  "${(totalTime.toInt()) / 60}시간 ${(totalTime.toInt()) % 60}분"
                Log.d("totalSum 7.26", (totalSum5/60).toString() + " 총합") //25
                Log.d("totalSum 7.19", (totalSum4/60).toString() + " 총합") //25
                Log.d("totalSum 7.12", (totalSum3/60).toString() + " 총합") //25
                Log.d("totalSum 7.5", (totalSum2/60).toString() + " 총합") //25
                Log.d("totalSum 7.4~~", (totalSum1/60).toString() + " 총합") //25
                setData(totalSum1, totalSum2, totalSum3, totalSum4, totalSum5)
            }
        }
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
    }
    private fun setData(one: Float, two: Float, three:Float, four:Float, five:Float) {
        Log.d("totalSum one", (one/60).toString())
        Log.d("totalSum2", (two/60).toString())
        Log.d("totalSum3", (three/60).toString())
        Log.d("totalSum4", (four/60).toString())
        Log.d("totalSum5", (five/60).toString())

        val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val dateNow: String? = LocalDateTime.now().format(textformatter) //오늘
        val targetYearMonth: YearMonth = YearMonth.from(LocalDate.parse(dateNow.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd")))
        val end = targetYearMonth.atEndOfMonth() //한달의 마지막 날
        val weekOfMonth: Int = end.get(ChronoField.ALIGNED_WEEK_OF_MONTH) //현재 날짜의 주일
        Log.d("asdfadsf",weekOfMonth.toString()) //마지막날의 주 사이즈 구하기
        val weekOfMonthSize : Int = (weekOfMonth-1)
        Log.d("asdfadsf",weekOfMonthSize.toString()) //마지막날의 주 사이즈 구하기


        val weekOfMonthValue = mutableListOf<Float>((one/60),(two/60),(three/60), (four/60),(five/60))
        val values = mutableListOf<BarEntry>()
        for(i in 0..weekOfMonthSize) {
            values.add(BarEntry(i.toFloat(),weekOfMonthValue.get(i)))
        }
//        barData.forEachIndexed { index, chartData ->
//            values.add(BarEntry(index.toFloat(), chartData.value))
//        }


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
