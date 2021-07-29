package kr.co.gooroomeelite.views.home


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.truncate




// 오타 줄이기 위해 상수 사용시 아래와 같이 선언 후 사용
const val STUDY_TIME = "nowstuytime"


class StopwatchFragment : Fragment() {

    private lateinit var subject : Subject
    private lateinit var documentId : String

    private lateinit var buttonStartPause: Button
    private lateinit var buttonRestart: Button
    private lateinit var buttonEnd: Button
    private lateinit var buttonFinish: Button

    private var stopwatch: Chronometer? = null
    private var pauseOffset: Long = 0
    private var running = false                                                                     // 스탑워치 실행중
    private var curTime: Long = 0                                                                   // 공부 진행시간
    var hour : Int = 0
    var minute : Int = 0
    var second : Int = 0
    var hourMinute : Int = 0
    var sum : Int = 0

    var firestore : FirebaseFirestore? = null
    val intent = Intent()

    private val todayStudyTime = MutableLiveData<Int>()
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    override fun onCreateView(
        @Nullable inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_stopwatch, container, false)
        val subject = arguments?.getSerializable("subject")
        val documentId = arguments?.getString("documentId")
        val firestore = FirebaseFirestore.getInstance()

        Log.d("TTTTTT","${subject}")
        stopwatch = v.findViewById((R.id.stopwatch))
        stopwatch?.base = SystemClock.elapsedRealtime()

        buttonStartPause = v.findViewById(R.id.btn_start)   //시작
        buttonEnd = v.findViewById(R.id.btn_end)    //기록 종료

        buttonRestart = v.findViewById(R.id.btn_restart)    //다시시작
        buttonFinish = v.findViewById(R.id.btn_finish)  //일시정지

        buttonStartPause.visibility = View.VISIBLE
        buttonEnd.visibility = View.GONE
        buttonRestart.visibility = View.GONE
        buttonFinish.visibility = View.GONE


        // 버튼 클릭 이벤트
        buttonStartPause.performClick()
        buttonEnd.performClick()
        buttonRestart.performClick()
        buttonFinish.performClick()



//        // 현재 타이머 값 표시 (시, 분, 초 사이 공백 넣기)
        stopwatch?.setOnChronometerTickListener{ stopwatch ->
            val elapsedMillis = SystemClock.elapsedRealtime() - stopwatch!!.base
            val h = (elapsedMillis / 3600000).toInt()
            val m = (elapsedMillis - h * 3600000).toInt() / 60000
            val hPlusm =  (elapsedMillis- (elapsedMillis/360000)*360000).toInt()/60000
            val s = (elapsedMillis - h * 3600000 - m * 60000).toInt() / 1000
            val hh = if (h < 10) "0$h" else h.toString() + ""
            val mm = if (m < 10) "0$m" else m.toString() + ""
            val ss = if (s < 10) "0$s" else s.toString() + ""
            stopwatch.format = "$hh : $mm : $ss"
            hour = h
            minute = m
            second = s
            hourMinute = (h*60)+m
            sum = hPlusm
        }
        stopwatch!!.base = SystemClock.elapsedRealtime()
        buttonStartPause.setOnTouchListener(View.OnTouchListener { v, event ->
            // pauseStopwatch()
            dayStartTimeStamp()
            buttonFinish.visibility = View.VISIBLE
            v.visibility = View.GONE

            startStopwatch()
            true
        })


        buttonFinish.setOnClickListener(View.OnClickListener {
            buttonRestart.visibility = View.VISIBLE
            buttonEnd.visibility = View.VISIBLE
            it.visibility = View.GONE
            buttonStartPause.visibility = View.GONE

            //일시정지 버튼을 눌렀을 때 해야하는 동작처리하기

            pauseStopwatch()

        })
        buttonRestart.setOnClickListener(View.OnClickListener {
            buttonFinish.visibility = View.VISIBLE
            it.visibility = View.GONE
            buttonEnd.visibility = View.GONE
            buttonStartPause.visibility = View.GONE

            //재시작 버튼을 눌렀을 때 해야하는 동작 처리하기
            startStopwatch()
        })

        buttonEnd.setOnClickListener(View.OnClickListener {  // -------------------------------------------
            studytimeupdate()
            timestamp()
            //기록 종료를 눌렀을 때 해야하는 이벤트 처리
            val intent = Intent(requireContext(), StudyEndActivity::class.java)

            intent.putExtra(STUDY_TIME, curTime)
            intent.putExtra("subject", subject)
            intent.putExtra("hour",hour)
            intent.putExtra("minute",minute)
            startActivity(intent)
            resetStopwatch()
            //파이어베이스에 현재 공부한 시간 업데이트
            activity?.finish()

        })
        return v
    }



    // 스탑워치 시작
    private fun startStopwatch() {
        if (!running) {
            // if은 true 여부 판별, true일 경우 안의 내용 실행 (이 경우 true가 아닐경우 실행)
            // running 앞에 !는 반대로 이해 -> true면 false false면 true로 비교
            curTime =
                SystemClock.elapsedRealtime() - pauseOffset  //현재 시간 -  pauseOffset : 동작시간 - 시작시간

            // 스탑워치 진행 시간 계산식
            stopwatch!!.base =
                curTime                                                                                  //  stopwatch!!.base에 curTime에 넣은 실시간 셋팅
            stopwatch!!.start()                                                                         // 스탑워치 시작 함수 실행
            running = true
        }
    }


    // 스탑워치 일시정지
    private fun pauseStopwatch() {
        if (running) {
            stopwatch!!.stop()                                                                          // 스탑워치 종료 함수 실행
            pauseOffset = SystemClock.elapsedRealtime() - stopwatch!!.base                              // pauseOffset : 동작시간 - 시작시간
            running = false                                                                                // 스탑워치 실행중일 경우 다시 시작하도록 false 넣음

            // 2. 정지버튼으로 전환 - 시작Btn : gone, 정지 Btn : Visible, 완료/계속 Btn : gone
            //buttonStartPause!!.text = "시작하기"                                                           // buttonStartPause에 text "Start" 바꾸고 종료

            //buttonStartPause!!.setBackgroundDrawable(ContextCompat.getDrawable(Context, R.drawable.ic_btn_restart))   //XML Selector 사용
        }
    }


    // 스탑워치 시간 초기화 (초기화와 동시에 나가기 화면 연결)
    private fun resetStopwatch() {
        stopwatch!!.base = SystemClock.elapsedRealtime()                                                // stopwatch!!.base는 동작시간????
        pauseOffset = 0                                                                                 // 진행시간 (pauseOffset) 0으로 초기화

    }


//!!! 프래그먼트 실행중 !!!



    // SharedPreferences는 데이터를 파일로 저장 -> 파일이 앱 폴더 내에 저장
// 저장 파일 위치 : data/data/(package_name)/shared_prefs/SharedPreference
// 다른 액티비티가 화면을 완전히 가리게 되면, 호출
// 유저가 다시 해당 액티비티를 호출하면 데이터가 다시 복원될 수 있는 상태
    override fun onPause() {
        super.onPause()
        val prefs = requireActivity().getSharedPreferences(                              // getPreference() 함수 : 자동으로 액티비티 이름의 파일 내에 저장함
                SW_PREFS,
                Context.MODE_PRIVATE                                              // Mode = 접근 권한, PRIVATE = 해당 앱에서만 접근 가능하게 해줌
        )


        val editor = prefs.edit()                                                 // 데이터 기록을 위한 Editor
        editor.putLong(CUR_TIME, curTime)                                         // 데이터 저장 : editor.putString(key, value)
        editor.apply()                                                            // 데이터 저장시 editor를 사용, apply 적용해야 동작함

        if (stopwatch != null) {
        }
    }


    //유저에게 Fragment가 보이게 해줌
    override fun onStart() {
        super.onStart()
        val prefs = requireActivity().getSharedPreferences(                               // getPreference() 함수 : 자동으로 액티비티 이름의 파일 내에 저장함
                SW_PREFS,
                Context.MODE_PRIVATE                                               // Mode = 접근 권한, PRIVATE = 해당 앱에서만 접근 가능하게 해줌
        )
        curTime = prefs.getLong(CUR_TIME, 0)
        if (running) {
            curTime = -SystemClock.elapsedRealtime() - stopwatch!!.base
        }
    }


    companion object {                                                                              // 동반자 객체 (companion object) : 클래스 안에 포함되는 이름 없는 객체
        // 어떤 클래스의 모든 인스턴스가 공유하는 객체를 만들고 싶을 때 사용 (java - static 효과)
        private const val SW_PREFS = "sWPrefs"
        private const val CUR_TIME = "curTime"
    }

    private fun studytimeupdate() {
        firestore?.collection("users")?.document(LoginUtils.getUid()!!)?.get()
            ?.addOnSuccessListener {
                val todaystudy = it.toObject(ContentDTO::class.java)
                val studytime = curTime.toInt()
                val totalstudy = todaystudy?.todaystudytime?.plus(studytime)
                val addStudyTime: Int = studytime
                firestore!!.collection("users").document(LoginUtils.getUid()!!)
                    .update("todaystudytime", totalstudy)
//                firestore!!.collection("subject").document(LoginUtils.getUid()!!).update("addStudyTime",addStudyTime)
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayStartTimeStamp(){
        val subject = arguments?.getSerializable("subject") as kr.co.gooroomeelite.entity.Subject
        val subjectStudyTime = subject.studytime //총 공부시간
        val studytimeCopy : Int = subjectStudyTime.plus(hourMinute.toInt()) //과목별 공부시간 + 스톱워치 기록 (총시간) 1초가 30
        val textformatters: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm부 ss초")
        val dayStartTime : LocalDateTime = LocalDateTime.now()
        val dayStartTimeValue : String = dayStartTime.format(textformatters) //"시작하기" 시간
        Log.d("dayStartTime",dayStartTimeValue.toString())

        FirebaseFirestore
            .getInstance()
            .collection("subject")
            .whereEqualTo("uid", LoginUtils.getUid())
            .whereEqualTo("name", subject.name.toString())
            .get().addOnSuccessListener {
                val subjectId = it.documents.get(0).id
                val subject = it.toObjects(Subject::class.java)
                FirebaseFirestore
                    .getInstance()
                    .collection("subject")
                    .document(subjectId)
                    .update("dayStartTime",dayStartTimeValue)

            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timestamp(){
        val subject = arguments?.getSerializable("subject") as kr.co.gooroomeelite.entity.Subject
        val subjectStudyTime = subject.studytime //총 공부시간
        val studytimeCopy : Int = subjectStudyTime.plus(hourMinute.toInt()) //과목별 공부시간 + 스톱워치 기록 (총시간) 1초가 30
        Log.d("timetime",hour.toString())
        Log.d("timetime",hourMinute.toString())
        Log.d("timetime",minute.toString())
        Log.d("timetime",second.toString())
        val textformatters: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm부 ss초")
        val dayStartTime : LocalDateTime = LocalDateTime.now()
        val dayEndtime : LocalDateTime = LocalDateTime.now()

        val dayStartTimeValue : String = dayStartTime.format(textformatters) //"시작하기" 시간
        val dayEndtimeValue : String = dayEndtime.format(textformatters) // "기록 종료" 시간

        Log.d("dayStartTimeEnd",dayEndtimeValue.toString())
        Log.d("dayStartTime",dayStartTimeValue.toString())

        FirebaseFirestore
            .getInstance()
            .collection("subject")
            .whereEqualTo("uid", LoginUtils.getUid())
            .whereEqualTo("name", subject.name.toString())
            .get().addOnSuccessListener {
                val subjectId = it.documents.get(0).id
                val subject = it.toObjects(Subject::class.java)
                FirebaseFirestore
                    .getInstance()
                    .collection("subject")
                    .document(subjectId)
                    .update("dayEndtime",dayEndtimeValue)
                FirebaseFirestore
                    .getInstance()
                    .collection("subject")
                    .document(subjectId)
                    .update("studytimeCopy",studytimeCopy)
                FirebaseFirestore
                    .getInstance()
                    .collection("subject")
                    .document(subjectId)
                    .update("studytime",studytimeCopy)
            }

        getTotalStudy()
    }
    override fun onDestroy() {
        super.onDestroy()
        stopwatch?.stop()

    }
    fun getTotalStudy() {
        Log.d("studytimetest","TEST")
        FirebaseFirestore.getInstance()
            .collection("subject")
            .whereEqualTo("uid", LoginUtils.getUid())
            .get()
            .addOnSuccessListener {

                Log.d("studytimetest","TEST2")
                val subject = it.toObjects(kr.co.gooroomeelite.entity.Subject::class.java)
                var studytimetodaylist = mutableListOf<Int>()
                for (i in 0..subject.size - 1) {
                    studytimetodaylist.add(subject[i].studytime)
                }
                todayStudyTime.value = studytimetodaylist.sum()


                Log.d("studytimetest","TEST3")
                FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(LoginUtils.getUid()!!)
                    .update("todaystudytime",todayStudyTime.value)

                Log.d("studytimetest","TEST4")
            }
        Log.e("TEST,","111")
    }
}
