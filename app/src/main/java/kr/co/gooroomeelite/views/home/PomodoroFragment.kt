package kr.co.gooroomeelite.views.home

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import kr.co.gooroomeelite.R
import java.util.*

// 뽀모도로 타이머
class PomodoroFragment : Fragment() {
    private var textViewPomodoro: TextView? = null      // 남은 반복 횟수
    private var textViewCycles: TextView? = null        // 남은 반복 횟수
    private var editTextInput1: EditText? = null
    private var buttonSet: Button? = null
    private var editTextInput2: EditText? = null
    private var editCyclesInput: EditText? = null
    private var buttonStartPause: Button? = null
    private var buttonReset: Button? = null
    private var textViewCycleTotal: TextView? = null
    private var textViewTime1: TextView? = null
    private var textViewTime2: TextView? = null
    private var countdownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var isSecondCycle = false
    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis = startTimeInMillis
    private var endTime: Long = 0
    private var startCycles: Long = 0
    private var cyclesLeft = startCycles
    private var otherCycleInMillis: Long = 0

    @Nullable
    override fun onCreateView(
            @NonNull inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_pomodoro, container, false)
        editCyclesInput = v.findViewById(R.id.cycles_edit_text_input)
        textViewCycles = v.findViewById(R.id.text_view_cycles)
        textViewCycleTotal = v.findViewById(R.id.cycles_total)
        textViewTime1 = v.findViewById(R.id.time1_total)
        textViewTime2 = v.findViewById(R.id.time2_total)
        editTextInput1 = v.findViewById(R.id.edit_text1_input)
        textViewPomodoro = v.findViewById(R.id.text_view_pomodoro)
        buttonStartPause = v.findViewById(R.id.button_start_pause)
        buttonReset = v.findViewById(R.id.button_reset)
        buttonSet = v.findViewById(R.id.button1_set)
        editTextInput2 = v.findViewById(R.id.edit_text2_input)

        buttonSet?.setOnClickListener(View.OnClickListener {
            val input1 = editTextInput1?.getText().toString()
            val input2 = editTextInput2?.getText().toString()
            var cyclesIn = editCyclesInput?.getText().toString()
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
            editTextInput1?.setText("")
            editTextInput2?.setText("")
            editCyclesInput?.setText("")
            textViewCycleTotal?.setText("$cyclesIn Cycles")
            textViewTime1?.setText("$input1 Minutes")
            textViewTime2?.setText("$input2 Minutes")
        })
        buttonStartPause?.setOnClickListener(View.OnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        })
        buttonReset?.setOnClickListener(View.OnClickListener { resetTimer() })
        return v
    }

    private fun setTime(millisecs: Long, c: Long) {
        startTimeInMillis = millisecs
        startCycles = c
        resetTimer()
        closeKeyboard()
    }

    private fun startTimer() {
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
                    }
                    isSecondCycle = !isSecondCycle
                    setTime(otherCycleInMillis, cyclesLeft)
                    otherCycleInMillis = t
                    textViewCycles!!.text = "남은 반복 횟수 : $cyclesLeft"
                    if (cyclesLeft > 0) {
                        startTimer()
                    } else {
                        onFinish()
                    }
                } else {
                    isTimerRunning = false
                    buttonStartPause!!.text = "Start"
                    updateInterface()
                }
            }
        }.start()
        isTimerRunning = true
        updateInterface()
    }

    private fun pauseTimer() {
        countdownTimer!!.cancel()
        isTimerRunning = false
        updateInterface()
    }

    private fun resetTimer() {
        timeLeftInMillis = startTimeInMillis
        cyclesLeft = startCycles
        updatePomodoroText()
        updateInterface()
    }

    private fun updatePomodoroText() {
        val hours = (startTimeInMillis / 1000).toInt() / 3600
        val mins = (timeLeftInMillis / 1000 % 3600).toInt() / 60
        val secs = (timeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted: String
        timeLeftFormatted = if (hours > 0) {
            String.format(
                    Locale.getDefault(),
                    "%d:%02d:%02d",
                    hours,
                    mins,
                    secs
            )
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
        }
        textViewPomodoro!!.text = timeLeftFormatted
        textViewCycles!!.text = "남은 반복 횟수 : $cyclesLeft"
    }

    private fun updateInterface() {
        if (isTimerRunning) {
            editCyclesInput!!.visibility = View.INVISIBLE
            editTextInput1!!.visibility = View.INVISIBLE
            editTextInput2!!.visibility = View.INVISIBLE
            buttonSet!!.visibility = View.INVISIBLE
            buttonReset!!.visibility = View.INVISIBLE
            buttonStartPause!!.text = "Pause"
            textViewCycleTotal!!.visibility = View.VISIBLE
            textViewTime2!!.visibility = View.VISIBLE
            textViewTime1!!.visibility = View.VISIBLE
        } else {
            editTextInput1!!.visibility = View.VISIBLE
            editTextInput2!!.visibility = View.VISIBLE
            editCyclesInput!!.visibility = View.VISIBLE
            buttonSet!!.visibility = View.VISIBLE
            buttonStartPause!!.text = "Start"
            textViewCycleTotal!!.visibility = View.INVISIBLE
            textViewTime2!!.visibility = View.INVISIBLE
            textViewTime1!!.visibility = View.INVISIBLE
            if (timeLeftInMillis < 1000) {
                buttonStartPause!!.visibility = View.INVISIBLE
            } else {
                buttonStartPause!!.visibility = View.VISIBLE
            }
            if (timeLeftInMillis < startTimeInMillis) {
                buttonReset!!.visibility = View.VISIBLE
            } else {
                buttonReset!!.visibility = View.INVISIBLE
            }
        }
    }

    fun closeKeyboard() {

        val view = activity?.currentFocus
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        /*val view = activity!!.currentFocus
        if (view != null) {
            val imm = activity!!.getSystemService(context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }*/
    }

    override fun onStop() {
        super.onStop()
        val prefs = activity!!.getSharedPreferences(
                POM_PREFS,
                Context.MODE_PRIVATE
        )
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
        val prefs = activity!!.getSharedPreferences(
                POM_PREFS,
                Context.MODE_PRIVATE
        )
        startTimeInMillis = prefs.getLong(START_MILLIS, 600000)
        timeLeftInMillis = prefs.getLong(MILLIS_LEFT, startTimeInMillis)
        isTimerRunning = prefs.getBoolean(TIMER_RUNNING, false)
        updatePomodoroText()
        updateInterface()
        if (isTimerRunning) {
            endTime = prefs.getLong(END_TIME, 0)
            timeLeftInMillis = endTime - System.currentTimeMillis()
            updatePomodoroText()
            updateInterface()
            if (cyclesLeft < 0 && timeLeftInMillis < 0) {
                timeLeftInMillis = 0
                isTimerRunning = false
                updatePomodoroText()
                updateInterface()
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
}



/*class PomodoroFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_pomodoro, container, false)
    }

}*/