package kr.co.gooroomeelite.views.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import kr.co.gooroomeelite.R

/*
보통 리스너는 버튼클릭같은거 입력같은거 할때 자동 실행되는 이벤트함수
뷰 만들어서 변수에 뷰 인자 맞춰끼우고
이벤트 발생하면 리스너 실행
리스너 실행하면서 안에있는 함수실행 끝
*/


// 스탑워치
class StopwatchFragment : Fragment() {
    private var buttonStartPause: Button? = null                                                        // 스탑워치 시작버튼
    private var buttonReset: Button? = null                                                             // 스탑워치 리셋버튼
    private var stopwatch: Chronometer? = null                                                          // 시간 측정
    private var pauseOffset: Long = 0                                                                   // 일시중지
    private var running = false                                                                         // 스탑워치 실행중
    private var curTime: Long = 0                                                                       // curTime : 현재시간, 타입 Long 맞음

    @Nullable                                                                                           // @Nullable : 무조건 null인지 확인
    // (@는 어노테이션 - 용도 : 문서화, 컴파일러 체크, 코드 분석용도 명시 : 패키지,클래스,메소드, 프로퍼티, 변수에 명시가능)

    override fun onCreateView(                                                                          // override : 모든 메서드에 대해서 붙여서 스펠링 에러 확인 가능
            // 현재 메소드가 수퍼클래스의 메소드를 오버라이드한 메소드임을 컴파일러에게 명시한다. 만일 수퍼클래스에 해당하는 메소드가 없다면 컴파일러가 인지하고 에러를 발생시켜 준다.

           @NonNull inflater: LayoutInflater,                                                          // @NonNull : null 일 수 없고, null 일 수도 있다는 애너테이션
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_stopwatch, container, false)              // 매개변수 설명 : inflate( 1.객체화하고픈 xml파일, 2.객체화한 뷰를 넣을 부모 레이아웃/컨테이너, 3.true(바로 인플레이션 하고자 하는지)) // R : res 폴더, layout : R의 내부 클래스

        stopwatch = v.findViewById(R.id.stopwatch)                                                      // 레이아웃 안에 있는 View들을 가져와 사용하는 메소드 (findViewByld)
        stopwatch?.setFormat("%s")                                                                      // stopwatch 포멧 문자 타입으로 변환
        stopwatch?.setBase(SystemClock.elapsedRealtime())                                               // 시간측정시 사용, Import한 SystemClock에서 elapedRealtime의 계산식을 가져옴
        // SystemClock.elapsedRealtime() 부팅된 시점부터 현재까지의 시간을 millisecond로 리턴

        buttonStartPause = v.findViewById(R.id.button_start_pause)                                      // buttonStartPause 클릭시 findViewById를 이용하여 layout의 button_start_pause 가져옴
        buttonReset = v.findViewById(R.id.button_reset)                                                 // buttonReset 클릭시  findViewById를 이용하여 layout의 button_reset 가져옴

        // resetbutton 클릭시 상세동작
        buttonReset?.setOnClickListener(View.OnClickListener { resetStopwatch() })                      // buttonReset 클릭시 resetStopwatch() 실행

        // buttonStartPause 클릭시 상세동작
        buttonStartPause?.setOnClickListener(View.OnClickListener {                                     // buttonStartPause 클릭시
            if (running) {
                pauseStopwatch()                                                                        // 스탑워치 정지 실행
            } else {
                startStopwatch()                                                                        // 스탑워치 시작 실행
            }
        })
        return v                                                                                        // 변수 v(fragment_stopwatch.xml)에 결과 return
    }

    // 스탑워치 시작
    private fun startStopwatch() {
        if (!running) {                                                                                 // if은 true 여부 판별, true일 경우 안의 내용 실행 (이 경우 true가 아닐경우 실행)
                                                                                                        // running 앞에 !는 반대로 이해 -> true면 false false면 true로 비교

            curTime = SystemClock.elapsedRealtime() - pauseOffset                                       // 스탑워치 진행 시간 계산식
            stopwatch!!.base = curTime                                                                  //  stopwatch!!.base에 curTime에 넣은 실시간 셋팅
            stopwatch!!.start()                                                                         // 스탑워치 시작 함수 실행
            running = true                                                                              // 스탑워치가 실행중일 경우 다시시작 못하도록 ture 넣음
            buttonStartPause!!.text = "Pause"                                                           // buttonStartPause에 text "Pause" 바꾸고 종료
            Log.d("aaa",pauseOffset.toString())
        }
    }

    // 스탑워치 종료
    private fun pauseStopwatch() {
        if (running) {
            stopwatch!!.stop()                                                                          // 스탑워치 종료 함수 실행
            pauseOffset = SystemClock.elapsedRealtime() - stopwatch!!.base                              // pauseOffset : 동작시간 - 시작시간
            running = false                                                                             // 스탑워치 실행중일 경우 다시 시작하도록 false 넣음
            buttonStartPause!!.text = "Start"                                                           // buttonStartPause에 text "Start" 바꾸고 종료
        }
        Log.d("aaa2",pauseOffset.toString())
    }

    // 스탑워치 시간 초기화
    private fun resetStopwatch() {
        stopwatch!!.base = SystemClock.elapsedRealtime()                                                // stopwatch!!.base는 동작시간????
        pauseOffset = 0                                                                                 // 진행시간 (pauseOffset) 0으로 초기화
    }


    //
    override fun onStop() {
        super.onStop()
        val prefs = activity!!.getSharedPreferences(
                SW_PREFS,
                Context.MODE_PRIVATE
        )
        val editor = prefs.edit()
        editor.putLong(CUR_TIME, curTime)
        editor.apply()
        if (stopwatch != null) {
        }
        Log.d("aaa3",curTime.toString())
    }


    //
    override fun onStart() {
        super.onStart()
        val prefs = activity!!.getSharedPreferences(
                SW_PREFS,
                Context.MODE_PRIVATE
        )
        curTime = prefs.getLong(CUR_TIME, 0)
        if (running) {
            curTime = -SystemClock.elapsedRealtime() - stopwatch!!.base
        }
    }

    companion object {                                                                              // 동반자 객체 (companion object) : 클래스 안에 포함되는 이름 없는 객체
                                                                                                    // 어떤 클래스의 모든 인스턴스가 공유하는 객체를 만들고 싶을 때 사용 (java - static 효과)
        private const val SW_PREFS = "sWPrefs"
        private const val CUR_TIME = "curTime"
    }
}

/*class StopwatchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false)
    }

}*/