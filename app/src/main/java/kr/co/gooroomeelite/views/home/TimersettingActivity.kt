package kr.co.gooroomeelite.views.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_timersetting.*
import kotlinx.android.synthetic.main.activity_timersetting.btn_back
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityTimersettingBinding
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.views.login.LoginActivity


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
        }

        // 뽀모도로 설정 - 집중시간 Btn
        btn_focustime.setOnClickListener {
            val mfocustTimeView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_focustime, null)
            val mBuilder = AlertDialog.Builder(this).setView(mfocustTimeView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            var minutePciker : NumberPicker = mfocustTimeView.findViewById(R.id.minutepicker)
            val okButton = mfocustTimeView.findViewById<TextView>(R.id.ok_btn)
            val cancelButton = mfocustTimeView.findViewById<TextView>(R.id.cancel_btn)

            minutePciker.minValue = 0
            minutePciker.maxValue = 12//13개
            minutePciker.displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55","60")
            okButton.setOnClickListener {
                //ok눌렀을때
                mAlertDialog.dismiss()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
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
