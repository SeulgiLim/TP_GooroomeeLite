package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_timersetting.*
import kotlinx.android.synthetic.main.activity_timersetting.btn_back
import kotlinx.android.synthetic.main.activity_timersetting_focusttime_dialog.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityTimersettingBinding


// 타이머 상세설정 화면


/*class PomoFocusDialog(private val owner:TimersettingActivity) : Dialog(owner.TimersettingActivity) { //

private lateinit var binding: ActivityPomoFocusDialogBinding

override fun onCreate(savedInstanceState: Bundle?) {
window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
requestWindowFeature(Window.FEATURE_NO_TITLE)

super.onCreate(savedInstanceState)
// setContentView(R.layout.activity_pomo_focus_dialog)
binding = ActivityPomoFocusDialogBinding.inflate(layoutInflater)
setContentView(binding.root)

binding.hourPicker.minValue = 0
binding.hourPicker.maxValue = 23
binding.hourPicker.displayedValues = arrayOf(
    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
    "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
)

binding.minutePicker.minValue = 0
binding.minutePicker.maxValue = 5//6개
binding.minutePicker.displayedValues = arrayOf("00", "10", "20", "30", "40", "50")

binding.cancelBtn.setOnClickListener {
    dismiss()
}

/*
// owner를 못찾음
binding.okBtn.setOnClickListener {
    owner.setStudyTimeCallback(binding.hourPicker.value * 60 + binding.minutePicker.value * 10)
    dismiss()
}*/
}
}*/

class TimersettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimersettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimersettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_timersetting)


        // TimersettingActivity에서 StudyActivity로 이동
        btn_back.setOnClickListener {
            onBackPressed()
        }

        // 일반모드 설정
        btn_nomelmode.setOnClickListener {
            visibility()
        }


        // 뽀모도르 설정
        btn_pomodoromode.setOnClickListener {
            visibility()
            /* // 뽀모도르 라디오 버튼 선택시 시간설정 활성화
            isLoading.observe(this) {
                binding.RadioButton.visibility = if (it) View.VISIBLE else View.GONE
            }*/
        }

        // 뽀모도로 세부 설정
        // 뽀모도로 설정 - 집중시간 Btn
        btn_focustime.setOnClickListener {


            val view = View.inflate(this@TimersettingActivity, R.layout.activity_timersetting_focusttime_dialog, null)

            val builder = AlertDialog.Builder(this@TimersettingActivity)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


//
//            val builder = AlertDialog.Builder(this)
//
//            val custom_view = layoutInflater.inflate(R.layout.activity_timersetting_focusttime_dialog, null)
//            builder.setView(custom_view)
//
//            ok_btn.setOnClickListener {
//                custom_view.run {
//
//                }
//            }
//
//            cancel_btn.setOnNe

//            binding.cancelBtn.setOnClickListener {
//                dismiss()
//            }
//
//            binding.okBtn.setOnClickListener {
//
//                dismiss()

        }


        // 뽀모도로 설정 - 휴식시간 Btn
        btn_resettime.setOnClickListener {

        }

        // 뽀모도로 설정 - 장기 휴식 시간 Btn

        btn_longresettime.setOnClickListener {

        }


        // 뽀모도로 설정 - 세트 Btn

        btn_settime.setOnClickListener {

        }
    }

    fun visibility() {
        if (btn_pomodoromode.isChecked) {
            Pomolayout.visibility = View.VISIBLE
        } else {
            Pomolayout.visibility = View.GONE
        }
    }

}



/*binding.btnTimermode.setOnClickListener {
    val StopwatchFragment: StopwatchFragment = StopwatchFragment()
    val fragmentManager: FragmentManager = supportFragmentManager


    val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
    fragmentTransaction.replace(R.id.container, StopwatchFragment)            // 할 일
    fragmentTransaction.commit()                                        // 끝
}*/
