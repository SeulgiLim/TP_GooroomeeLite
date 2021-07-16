package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_study_end.*
import kotlinx.coroutines.flow.callbackFlow
import kr.co.gooroomeelite.databinding.ActivityStudyEndBinding
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import javax.security.auth.Subject


class StudyEndActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStudyEndBinding
    var firestore : FirebaseFirestore? = null
    var bundle: Bundle? = null
    var subjectname: String? = null
    var studytime: Int? = null
    private lateinit var subject : kr.co.gooroomeelite.entity.Subject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyEndBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        // 측정된 공부시간 데이터 추출
        val nowstudytime = intent.getLongExtra(STUDY_TIME, 0L).toInt()
        binding.btnStudyend.setOnClickListener{
            finish()
        }


//        bundle = intent.getBundleExtra("bundle")
//        studytime = bundle?.getInt("studytime")
//        val studytime = 20
//        val subjectname = "과학"

        setting()

    }



    private fun setting() {
        firestore?.collection("users")?.document(getUid()!!)?.get()?.addOnSuccessListener {
            val subject = it.toObject(ContentDTO::class.java)
//            val studytime = 20
            val subjectname = intent.getSerializableExtra("subject") as kr.co.gooroomeelite.entity.Subject
//            subject?.todaystudytime = subject?.todaystudytime?.plus(studytime)
            val today = subject?.todaystudytime!!
            val goal = subject?.studyTime!!
//            firestore!!.collection("users").document(getUid()!!).update(
//                "todaystudytime",
//                subject?.todaystudytime
//            )
            binding.tvStudyendStudytotal.text = subject.todaystudytime.toString()
            binding.tvStudygoal.text =
                "%02d".format(goal.div(60)) + "시간 " + "%02d".format(
                    goal.rem(
                        60
                    )
                ) + "분"
            binding.tvStudyendSubject.text = subjectname.name
            val percent: Int = (((today * 100 / (goal)) / 100.toFloat()) * 100).toInt()
            if (percent >= 100) {
                binding.tvStudyendSecond.text = "100%"
                binding.seekBar.progress = percent
                binding.tvStudyendStudytotal.text =
                    "%02d".format(today.div(60)) + "시간 " + "%02d".format(
                        today.rem(
                            60
                        )
                    ) + "분"
            } else {
                binding.tvStudyendSecond.text = "${percent}%"
                binding.tvStudyendStudytotal.text =
                    "%02d".format(today.div(60)) + "시간 " + "%02d".format(
                        today.rem(
                            60
                        )
                    ) + "분"
                binding.seekBar.progress = percent
            }
        }
    }
}
