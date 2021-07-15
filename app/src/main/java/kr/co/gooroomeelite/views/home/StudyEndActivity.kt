package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_study_end.*
import kr.co.gooroomeelite.R

class StudyEndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_end)

        // 측정된 공부시간 데이터 추출
        val nowstudytime = intent.getLongExtra(STUDY_TIME, 0L)
        //val subject = intent.getSerializableExtra(subject, )
        //val documentId = intent.getLongExtra(documentId, )

        Log.e("[TEST]", "${intent.getLongExtra(STUDY_TIME, 0L)}")

        // 가져온 데이터 (공부진행시간 제대로 가져왔는지 보여주기 Test)
        textView10.append("공부진행시간 : ${nowstudytime}\n")
        textView11.append("과목명 : ${nowstudytime}\n")
        textView12.append("과목ID : ${nowstudytime}\n")

        // Bundle을 통해 한번에 모든 데이터 얻기
        // val Bundle: Bundle = intent.getBundleExtra("nowstudy")

        // finish() -> onCreate에서 finish() 넣으면 실행되고 바로 종료됨 삭제 할 것!!

    }
}