package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_timersetting.*
import kotlinx.android.synthetic.main.activity_timersetting.btn_back
import kr.co.gooroomeelite.R


// 타이머 상세설정 화면

class TimersettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timersetting)

        /*binding.btnTimermode.setOnClickListener {
            val StopwatchFragment: StopwatchFragment = StopwatchFragment()
            val fragmentManager: FragmentManager = supportFragmentManager


            val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
            fragmentTransaction.replace(R.id.container, StopwatchFragment)            // 할 일
            fragmentTransaction.commit()                                        // 끝
        }*/

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

    }

    fun visibility() {
        if (btn_pomodoromode.isChecked) {
            Pomolayout.visibility = View.VISIBLE
        } else {
            Pomolayout.visibility = View.GONE
        }
    }
}

