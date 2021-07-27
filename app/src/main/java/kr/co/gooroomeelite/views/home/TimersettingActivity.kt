package kr.co.gooroomeelite.views.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.util.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_timersetting.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityTimersettingBinding


class TimersettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimersettingBinding
    var check : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val pref = getSharedPreferences("check",0)
        val editor = pref.edit()
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
                binding.textFocustime.text = minutePciker.value.times(5).toString()
                mAlertDialog.dismiss()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }


        // 뽀모도로 설정 - 휴식시간 Btn
        btn_resettime.setOnClickListener {
            val mrestTimeView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_resttime, null)
            val mBuilder = AlertDialog.Builder(this).setView(mrestTimeView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            var minutePciker : NumberPicker = mrestTimeView.findViewById(R.id.minutepicker)
            val okButton = mrestTimeView.findViewById<TextView>(R.id.ok_btn)
            val cancelButton = mrestTimeView.findViewById<TextView>(R.id.cancel_btn)

            minutePciker.minValue = 0
            minutePciker.maxValue = 12//13개
            minutePciker.displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55","60")
            okButton.setOnClickListener {
                //ok눌렀을때
                binding.textResttime.text = minutePciker.value.times(5).toString()
                mAlertDialog.dismiss()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }

        // 뽀모도로 설정 - 장기 휴식 시간 Btn

        btn_longresettime.setOnClickListener {
            val mlongrestTimeView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_longresttime, null)
            val mBuilder = AlertDialog.Builder(this).setView(mlongrestTimeView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            var minutePciker : NumberPicker = mlongrestTimeView.findViewById(R.id.minutepicker)
            val okButton = mlongrestTimeView.findViewById<TextView>(R.id.ok_btn)
            val cancelButton = mlongrestTimeView.findViewById<TextView>(R.id.cancel_btn)

            minutePciker.minValue = 0
            minutePciker.maxValue = 12//13개
            minutePciker.displayedValues = arrayOf("00","05","10","15","20","25","30","35","40","45","50","55","60")
            okButton.setOnClickListener {
                //ok눌렀을때
                binding.textLongresettime.text = minutePciker.value.times(5).toString()
                mAlertDialog.dismiss()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }


        // 뽀모도로 설정 - 세트 Btn

        btn_settime.setOnClickListener {
            val msetTimeView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_settime, null)
            val mBuilder = AlertDialog.Builder(this).setView(msetTimeView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            var minutePciker : NumberPicker = msetTimeView.findViewById(R.id.minutepicker)
            val okButton = msetTimeView.findViewById<TextView>(R.id.ok_btn)
            val cancelButton = msetTimeView.findViewById<TextView>(R.id.cancel_btn)

            minutePciker.minValue = 0
            minutePciker.maxValue = 9//10개
            minutePciker.displayedValues = arrayOf("1","2","3","4","5","6","7","8","9","10")
            okButton.setOnClickListener {
                //ok눌렀을때
                binding.textSettime.text = minutePciker.value.plus(1).toString()
                mAlertDialog.dismiss()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }

    fun visibility() {
        val pref = getSharedPreferences("check",0)
        val editor = pref.edit()
        if (btn_pomodoromode.isChecked) {
            Pomolayout.visibility = View.VISIBLE
            check = true
            editor.putBoolean("check",true).apply()

        } else {
            Pomolayout.visibility = View.GONE
            check = false
            editor.putBoolean("check",false).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        val pref = getSharedPreferences("check",0)
        val editor = pref.edit()
        editor.putBoolean("check",false).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = getSharedPreferences("check",0)
        val editor = pref.edit()
        editor.putString("focustime",binding.textFocustime.text.toString()).commit()
        editor.putString("resttime",binding.textResttime.text.toString()).commit()
        editor.putString("longresttime",binding.textLongresettime.text.toString()).commit()
        editor.putString("settime",binding.textSettime.text.toString()).commit()
    }
}
