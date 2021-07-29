package kr.co.gooroomeelite.views.home
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-27
 * @desc
 */
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_study_timer.*
import kotlinx.android.synthetic.main.fragment_pomodoro.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.views.login.LoginActivity
import java.util.*

// 뽀모도로 타이머
class PomodoroFragment(val owner : AppCompatActivity) : Fragment() {
    private var textViewPomodoro: TextView? = null      // 남은 반복 횟수
    private lateinit var buttonSet: Button
    private lateinit var buttonStart: Button
    private lateinit var buttonReset: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonEnd: Button
    private lateinit var buttonBack: Button

    private var countdownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var isSecondCycle = false
    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis = startTimeInMillis
    private var endTime: Long = 0
    private var startCycles: Long = 0
    private var cyclesLeft = startCycles
    private var otherCycleInMillis: Long = 0
    private var test : String = ""
    var mBackWait : Long = 0

    @Nullable
    override fun onCreateView(
            @NonNull inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View {
        val pref = requireActivity().getSharedPreferences("check",0)
        val focusttime = pref.getString("focustime","")!!
        val resttime = pref.getString("resttime","")!!
        val longresttime = pref.getString("longresttime","")!!
        val setime = pref.getString("settime","")!!


        val check = pref.getBoolean("check",false)

        val v = inflater.inflate(R.layout.fragment_pomodoro, container, false)
        textViewPomodoro = v.findViewById(R.id.text_view_pomodoro)
        buttonStart = v.findViewById(R.id.button_start)
        buttonReset = v.findViewById(R.id.button_reset)
        buttonSet = v.findViewById(R.id.button1_set)
        buttonPause = v.findViewById(R.id.button_pause)
        buttonEnd = v.findViewById(R.id.btn_end)
        buttonBack = v.findViewById(R.id.button_back)


        test = textViewPomodoro?.text.toString()
        Log.d("TESTESTEST","$focusttime")
        Log.d("TESTESTEST","$test")
        buttonStart.visibility = View.VISIBLE // 시작
        buttonSet.visibility = View.VISIBLE // 세팅
        buttonPause.visibility = View.GONE // 일시 정지
        buttonReset.visibility = View.GONE // 다시 시작
        buttonEnd.visibility = View.GONE // 종료
        buttonBack.visibility = View.GONE // 초기화

        buttonSet.setOnClickListener(View.OnClickListener {
            val input1 = focusttime
            val input2 = resttime
            var cyclesIn = setime
            if (input1.length == 0 || input2.length == 0) {
                Toast.makeText(context, "Field cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (cyclesIn.length == 0) {
                cyclesIn = "1"
            }
            val millisInput1 = input1.toLong() * 60000
            if (millisInput1 == 0L) {
                Toast.makeText(
                    context,
                    "Please enter a positive number",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            setTime(millisInput1, cyclesIn.toLong())
            otherCycleInMillis = input2.toLong() * 60000

            btnsetting(buttonSet)
        })

        buttonStart.setOnClickListener{
            startTimer()
            btnsetting(buttonStart)
        }

        buttonPause.setOnClickListener {
            pauseTimer()
            btnsetting(buttonPause)
        }

        buttonReset.setOnClickListener {
            startTimer()
            btnsetting(buttonReset)
        }


        buttonBack.setOnClickListener {
            resetTimer()
            btnsetting(buttonBack)
        }
        //기록 종료를 눌렀을때 무슨일이 일어날지.
        return v
    }
    private fun setTime(millisecs: Long, c: Long) {
        startTimeInMillis = millisecs
        startCycles = c
        resetTimer()
    }
    private fun startTimer() {
        text_focustime.text = "집중 시간"
        buttonStart.text = "집중 시간 시작"
        buttonPause.text = "일시정지"
        buttonEnd.text = "기록 종료"
        endTime = System.currentTimeMillis() + timeLeftInMillis
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updatePomodoroText()
            }
            override fun onFinish() {
                if (cyclesLeft > 0) {
                    val t = startTimeInMillis
                    if (isSecondCycle) {
                        cyclesLeft -= 1
                        text_focustime.text = "휴식 시간"
                        buttonStart.text = "휴식 시작"
                        buttonPause.text = "휴식 정지"
                        buttonEnd.text = "휴식 종료"
                    }
                    isSecondCycle = !isSecondCycle
                    setTime(otherCycleInMillis, cyclesLeft)

                    //휴식시간 예상. 여기서 text변경. false -> 타이머 true -> 휴식시간



                    otherCycleInMillis = t
                    if (cyclesLeft > 0) {
                        startTimer()
                    } else {
                        onFinish()
                    }
                } else {
                    isTimerRunning = false
                    buttonStart.visibility = View.VISIBLE // 시작
                    buttonPause.visibility = View.GONE // 일시 정지
                    buttonReset.visibility = View.GONE // 다시 시작
                    buttonEnd.visibility = View.GONE // 종료
                }
            }
        }.start()
        isTimerRunning = true
    }
    private fun pauseTimer() {
        countdownTimer!!.cancel()
        isTimerRunning = false
    }
    private fun resetTimer() {
        timeLeftInMillis = startTimeInMillis
        cyclesLeft = startCycles
        updatePomodoroText()
    }
    private fun updatePomodoroText() {
        val hours = (startTimeInMillis / 1000).toInt() / 3600
        val mins = (timeLeftInMillis / 1000 % 3600).toInt() / 60
        val secs = (timeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted: String
        timeLeftFormatted = if (hours > 0) {
            String.format(Locale.getDefault(),"%d:%02d:%02d",hours,mins,secs)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
        }
        textViewPomodoro!!.text = timeLeftFormatted
        test = timeLeftFormatted
    }
    override fun onStop() {
        super.onStop()
        val prefs = requireActivity().getSharedPreferences(POM_PREFS,Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putLong(START_MILLIS, startTimeInMillis)
        editor?.putLong(MILLIS_LEFT, timeLeftInMillis)
        editor?.putBoolean(TIMER_RUNNING, isTimerRunning)
        editor?.putLong(END_TIME, endTime)
        editor?.apply()
        if (countdownTimer != null) {
            countdownTimer!!.cancel()
        }
    }
    override fun onStart() {
        super.onStart()
        val prefs = requireActivity().getSharedPreferences(POM_PREFS,Context.MODE_PRIVATE)
        startTimeInMillis = prefs.getLong(START_MILLIS, 600000)
        timeLeftInMillis = prefs.getLong(MILLIS_LEFT, startTimeInMillis)
        isTimerRunning = prefs.getBoolean(TIMER_RUNNING, false)
        updatePomodoroText()
        if (isTimerRunning) {
            endTime = prefs.getLong(END_TIME, 0)
            timeLeftInMillis = endTime - System.currentTimeMillis()
            updatePomodoroText()
            if (cyclesLeft < 0 && timeLeftInMillis < 0) {
                timeLeftInMillis = 0
                isTimerRunning = false
                updatePomodoroText()
            } else {
                startTimer()
            }
        }
    }
    companion object {                                                                              // 동반자 객체(Companion object) : 클래스 안에 포함되는 이름 없는 객체
        // 어떤 클래스의 모든 인스턴스가 공유하는 객체를 만들고 싶을 때 사용
        private const val POM_PREFS = "pomPrefs"
        private const val START_MILLIS = "startTimeInMillis"
        private const val MILLIS_LEFT = "millisLeft"
        private const val TIMER_RUNNING = "timerRunning"
        private const val END_TIME = "endTime"
    }

    private fun btnsetting(button: Button){
        if (button == buttonStart){
            buttonStart.visibility = View.GONE
            buttonPause.visibility = View.VISIBLE
            buttonSet.visibility = View.GONE
        }
        else if (button == buttonPause){
            buttonPause.visibility = View.GONE
            buttonReset.visibility = View.VISIBLE
            buttonEnd.visibility= View.VISIBLE
            buttonBack.visibility= View.VISIBLE
        }
        else if(button == buttonReset){
            buttonPause.visibility=View.VISIBLE
            buttonReset.visibility=View.GONE
            buttonEnd.visibility=View.GONE
            buttonBack.visibility=View.GONE
        }
        else if(button == buttonBack){
            buttonStart.visibility=View.VISIBLE
            buttonPause.visibility=View.GONE
            buttonReset.visibility=View.GONE
            buttonEnd.visibility=View.GONE
            buttonBack.visibility=View.GONE
            buttonSet.visibility=View.VISIBLE
        }
        else if(button == buttonSet){
            buttonStart.visibility = View.VISIBLE
            buttonPause.visibility = View.GONE
            buttonReset.visibility = View.GONE
            buttonEnd.visibility = View.GONE
        }
    }

    @Override fun onBackPressed() {
        //터치간 시간을 줄이거나 늘리고 싶다면 2000을 원하는 시간으로 변경해서 사용하시면 됩니다.
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(activity,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_LONG).show()
        } else {
            requireActivity().finish() //액티비티 종료
        }
    }

}