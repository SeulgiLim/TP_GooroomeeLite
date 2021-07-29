package kr.co.gooroomeelite.views.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityStudyBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.views.common.MainActivity
import kr.co.gooroomeelite.views.mypage.MusicActivity


class  StudyActivity : AppCompatActivity() {

    private lateinit var subject: Subject
    private lateinit var documentId: String
    private lateinit var binding: ActivityStudyBinding
    var check : Boolean? = null
    var test : String?  = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //화면 꺼짐 방지 기능
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // HomeFragment에서 과목, 문서 ID 받아오기, 전역변수 지정하여, 과목 정보 받기 * 전역변수 : 메서드 밖, 즉 클래스 안에서 선언된 변수를 가져다 쓰는 변수
        val subject = intent.getSerializableExtra("subject") as Subject
        val documentId =
            intent.getSerializableExtra("documentId") as String                  // 서버에 시간 데이터 저장시 필요함 (저장 방법 결정 필요)
        val bottomsheetFragment =StudystatusBottomsheetFragment()
        // 공부현황 Btn
        // StudyActivity 내 Fragment 영역에 StopwatchFragment 같이 보여주기
            val stopwatchFragment = StopwatchFragment()
            val bundle = Bundle()
            bundle.putSerializable("subject", subject)
            bundle.putString("documentId", documentId)
            stopwatchFragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(R.id.container, stopwatchFragment)
                .commit()

        // 뒤로가기 (StudyActivity -> HomeFragment로 이동)
        // 일반 - 10. Study Actitity에서 뒤로가기 두번 클릭해야 HomeFragmet로 이동함
        Log.d("aaa1", "btnBack111")
        binding.btnBackwatch.setOnClickListener {
            val mStudyView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_study, null)
            val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(mStudyView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            val okButton = mStudyView.findViewById<Button>(R.id.btn_study_ok)
            val cancelButton = mStudyView.findViewById<Button>(R.id.btn_study_no)
            okButton.setOnClickListener {
                //화면이동
                startActivity(Intent(this, MainActivity::class.java))
                mAlertDialog.dismiss()
                finish()

            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()


            }
        }
        // 가져온 데이터 (과목명 제대로 가져왔는지 보여주기 Test)
        binding.modeName.append("${subject.name}\n")
        // ASMR 실행 버튼
        binding.btnNoise.setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }
        // 스톱워치 모드 변경 버튼
        binding.btnTimermode.setOnClickListener {
            val intent = Intent(this, TimersettingActivity::class.java)
            startActivity(intent)
        }
        // 공부 시간 현황 버튼
        binding.btnStudynow.setOnClickListener {
            // BottomSheet 연결
        bottomsheetFragment.show(supportFragmentManager, "BottomSheetDialog")
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        Log.d("studytest","1")
        val pref = getSharedPreferences("check",0)
        check = pref.getBoolean("check",false)

        Log.d("studytest","${check}")
        Log.d("studytest","${pref}")
        // StudyActivity 내 Fragment 영역에 StopwatchFragment 같이 보여주기
        val subject = intent.getSerializableExtra("subject") as Subject
        val documentId = intent.getSerializableExtra("documentId") as String
        val stopwatchFragment = StopwatchFragment()
            val pomodorFragment = PomoFocustFragment()
            val bundle = Bundle()
            bundle.putSerializable("subject", subject)
            bundle.putString("documentId", documentId)
            stopwatchFragment.arguments = bundle
            if (check == false) {
                supportFragmentManager.beginTransaction().replace(R.id.container, stopwatchFragment)
                    .commit()
            }
            else{
                supportFragmentManager.beginTransaction().replace(R.id.container,pomodorFragment)
                    .commit()
            }
        }

}
