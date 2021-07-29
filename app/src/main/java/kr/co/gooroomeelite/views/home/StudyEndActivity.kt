package kr.co.gooroomeelite.views.home
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-17
 * @desc
 */
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
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
    var hour : Int? = null
    var minute : Int? = null
    private lateinit var subject : kr.co.gooroomeelite.entity.Subject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hour = intent.getIntExtra("hour",0)
        minute = intent.getIntExtra("minute",0)

        binding.hour.text = hour.toString()
        binding.minute.text = minute.toString()

        firestore = FirebaseFirestore.getInstance()
        // 측정된 공부시간 데이터 추출
        val nowstudytime = intent.getLongExtra(STUDY_TIME, 0L).toInt()
        binding.btnStudyend.setOnClickListener{
            finish()
        }

        setting()
        settingtime()
    }



    private fun setting() {
        firestore?.collection("users")?.document(getUid()!!)?.get()?.addOnSuccessListener {
            val subject = it.toObject(ContentDTO::class.java)
            val subjectname = intent.getSerializableExtra("subject") as kr.co.gooroomeelite.entity.Subject
            val today = subject?.todaystudytime!!
            val goal = subject?.studyTime!!
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

    private fun settingtime(){
        when (hour) {
            in 0..9 -> binding.hour.text = "0" + hour.toString()
            in 10..99 -> binding.hour.text = hour.toString()
        }
        when (minute){
            in 0..9 -> binding.minute.text = "0"+ minute.toString()
            in 10..59 -> binding.minute.text = minute.toString()
        }
    }

}
