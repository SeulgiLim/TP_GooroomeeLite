package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_timersetting.*
import kotlinx.android.synthetic.main.activity_timersetting.btn_back
import kr.co.gooroomeelite.R


// 타이머 상세설정 화면

class TimersettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timersetting)

        // TimersettingActivity에서 StudyActivity로 이동
        btn_back.setOnClickListener {
            onBackPressed()
        }

        // 일반모드 설정
        btn_nomelmode.setOnClickListener {
            btn_nomelmode.isChecked = true // 기본 선택 항목 설정

        }


        // 뽀모도르 설정
        btn_pomodoromode.setOnClickListener {

           /* // 뽀모도르 라디오 버튼 선택시 시간설정 활성화
            isLoading.observe(this) {
                binding.RadioButton.visibility = if (it) View.VISIBLE else View.GONE
            }*/
        }

        }
    }

