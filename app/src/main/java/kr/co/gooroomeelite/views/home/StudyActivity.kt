package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.entity.Subject

class StudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val subject = intent.getSerializableExtra("subject") as Subject
        val documentId = intent.getSerializableExtra("documentId") as String
        Log.d("subject", subject.toString())
        Log.d("documentId", documentId)
    }
}