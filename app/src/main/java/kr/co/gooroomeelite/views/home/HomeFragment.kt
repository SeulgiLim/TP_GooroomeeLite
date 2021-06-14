package kr.co.gooroomeelite.views.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.databinding.FragmentHomeBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            if (isLogin()) {
                FirebaseAuth.getInstance().signOut()
            }

        }
        return binding.root
    }

    // 현재 날짜
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // binding 이 프로퍼티로 선언되어 있기 때문에 프래그먼트 전체에서 호출 가능
        // binding.위젯id.속성 = "값"
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy년 M월 dd일", Locale.KOREA).format(currentDateTime)

        binding.tvdate.text = dateFormat // 확인되지 않은 참조? -> 앞 binding. 추가
    }

    // 디데이
    fun dday(){
        val dateFormat = SimpleDateFormat("yyyyMMdd")

        val startDate = dateFormat.parse("20200925").time
        val endDate = dateFormat.parse("20210614").time
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0 )
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time

        println("두 날짜간의 차이(일) : ${(endDate - startDate) / (24*60*60*1000)}")
        println("시작일 부터 경과 일 : ${(today) - startDate / (24*60*60*1000)}")
        println("목표일 까지 남은 일 (D-Day) : ${(endDate - today) / (24*60*60*1000)}")

    }



    // 총 공부시간
    // 목표 공부시간


   /*// 현재시간을 msec 으로 구한다.
    var now = System.currentTimeMillis()

    // 현재시간을 date 변수에 저장한다.
    var date = Date(now)

    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    var sdfNow = SimpleDateFormat("MM/dd ")
    var sdfNow_year = SimpleDateFormat("yyyy")
    var sdfNow_month = SimpleDateFormat("MM")
    var sdfNow_Time = SimpleDateFormat("HH:00")
    var sdfNow_day = SimpleDateFormat("dd")
    var sdfNow_hour = SimpleDateFormat("HH")

    // nowDate 변수에 값을 저장한다.
    var formatDate = sdfNow.format(date)
    var formatYear = sdfNow_year.format(date)
    var formatMonth = sdfNow_month.format(date)
    var formatDay = sdfNow_day.format(date)
    var formatTime = sdfNow_Time.format(date)
    var formatHour = sdfNow_hour.format(date)
    */
}