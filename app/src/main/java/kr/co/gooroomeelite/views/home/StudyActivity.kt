package kr.co.gooroomeelite.views.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_study_end.*
import kotlinx.android.synthetic.main.fragment_week.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityStudyBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.views.common.MainActivity
import kr.co.gooroomeelite.views.login.LoginActivity
import kr.co.gooroomeelite.views.mypage.MusicActivity


class  StudyActivity : AppCompatActivity() {

    private lateinit var subject: Subject
    private lateinit var documentId: String

    // private lateinit var nowstudytime : Int
    private lateinit var binding: ActivityStudyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_study)

        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // HomeFragment에서 과목, 문서 ID 받아오기, 전역변수 지정하여, 과목 정보 받기 * 전역변수 : 메서드 밖, 즉 클래스 안에서 선언된 변수를 가져다 쓰는 변수
        val subject = intent.getSerializableExtra("subject") as Subject
        val documentId =
            intent.getSerializableExtra("documentId") as String                  // 서버에 시간 데이터 저장시 필요함 (저장 방법 결정 필요)

        val bottomsheetFragment =
            StudystatusBottomsheetFragment()                                  // 공부현황 Btn

        // StudyActivity 내 Fragment 영역에 StopwatchFragment 같이 보여주기
        if (savedInstanceState == null) {
            val stopwatchFragment = StopwatchFragment()
            val bundle = Bundle()
            bundle.putSerializable("subject", subject)
            bundle.putString("documentId", documentId)
            stopwatchFragment.arguments = bundle

            supportFragmentManager.beginTransaction().add(R.id.container, stopwatchFragment)
                .commit()
        }

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
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
            finish()
        }
        Log.d("aaa1", "btnBack222")

        // nowstudytime = subject.studytime

        Log.e("[TEST]", "${intent.getSerializableExtra("subject") as Subject}")


        // 가져온 데이터 (과목명 제대로 가져왔는지 보여주기 Test)
        mode_name.append("${subject.name}\n")


        // StudyActivity -> TimerFragment로 데이터 넘기기
        /*val bundle = Bundle()
        bundle.putSerializable("subject", subject)
        bundle.putString("documentId", documentId)

        val StopwatchFragment = StopwatchFragment()
        StopwatchFragment.arguments = bundle*/

        //프래그먼트에서 액티비티로 넘어온 데이터 받기
        //fragment 클래스의 onCreateView


        // 하단 ASMR, 스톱워치 모드 변경, 공부 시간 현황 버튼
        // ASMR 실행 버튼 (태수님 작업본 연결예정)
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
}

    /*
* */

    /*// 스탑워치 버튼
    binding.btnTimermode.setOnClickListener {
        val StopwatchFragment: StopwatchFragment = StopwatchFragment()
        val fragmentManager: FragmentManager = supportFragmentManager


        val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
        fragmentTransaction.replace(R.id.container, StopwatchFragment)            // 할 일
        fragmentTransaction.commit()                                        // 끝
    }*/



    // 화이트 노이즈 버튼
    /*btn_wn.setOnClickListener {

        // 공부시간 데이터 보내기 (intent (putExtra 넣기) : Activity/Fragment -> Activity)

        // 공부시간 데이터 보내기 (Bundle : Activity/Fragment -> Fragment)
        // 1) 이동할 Fragment 객체 생성
        // 2) Bundle 객체 생성 및 데이터 저장 -> bundle.putXXXX (name, value)
        // 3) Fragment 객체.argument = Bundle 객체

        // val HomeFragment = HomeFragment()
        // val bundle = Bundle()
        // bundle.putInt("pauseOffset",0)
        // HomeFragment.arguments = bundle     // fragment의 arguments에 담은 bundle을 넘겨줌


        val PomodoroFragment : PomodoroFragment = PomodoroFragment()
        val fragmentManager : FragmentManager = supportFragmentManager


        val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
        fragmentTransaction.replace(R.id.container, PomodoroFragment)            // 할 일
        fragmentTransaction.commit() // 끝

        Log.d("Study 3",subject.toString())
    }*/

    /*override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

        // 뒤로가기 (StudyActivity -> HomeFragment로 이동)
        // 일반 - 10. Study Actitity에서 뒤로가기 두번 클릭해야 HomeFragmet로 이동함 -> StudyActivity 내 Fragment 영역에 StopwatchFragment 같이 보여주기 선언 안함
        binding.wa.setOnClickListener{
            finish()
        }
    }*/

    /*override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        startActivity(Intent(this, HomeFragment::class.java))
        //super.onBackPressed()
        finish()
    }*/


    /*override fun onPause() {
        super.onPause()
        binding.btnBack.setOnClickListener{
            //onBackPressed()
            startActivity(Intent(this, HomeFragment::class.java))
            finish()
        }
    }*/
//}



// HomeFragment에 공부시간 데이터 전달
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                var currentImageUrl : Uri? = data?.data
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageUrl)
                    val timeIntent = Intent(this@StudyActivity, HomeFragment::class.java)
                    //Log.d("aaaacurrentImageUrl", currentImageUrl.toSt간 데이터 전달
                    //        val studytimeintent = Intent(this@StudyActivity, HomeFragment::class.java)
                    //        val bundle = Bundle()
                    //        bundle.putString("Int","60")
                    //        studytimeintent.putExtras(bundle)
                    //        startActivity(studytimeintent)ring())

                    galleryIntent.putExtra("gallery",currentImageUrl.toString())
                    startActivity(galleryIntent)
//                    binding.showImageView.setImageBitmap(bitmap)
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }else{
                Log.d("aaaa","something wrong")
            }
        }
    }*/


/*val tx: FragmentTransaction = fragmentManager.beginTransation()
tx.replace(R.id.fragment, MyFragment()).addToBackStack("tag").commit()*/

/*btn_back.setOnClickListener{
    onBackPressed()
}*/

//        binding.btnBack.setOnClickListener{
//            val HomeFragment: HomeFragment = HomeFragment()
//            val fragmentManager: FragmentManager = supportFragmentManager
//
//
//            val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
//            fragmentTransaction.replace(R.id.container, HomeFragment).addToBackStack("tag").commit()        // 할 일
//
//        }


// 타이머설정 버튼 클릭시 타이머 설정 화면으로 이동
/*binding.btnTimermode.setOnClickListener {
    val intent = Intent(this, TimersettingActivity::class.java)
    startActivity(intent)
}*/


/*binding.btnBack.setOnClickListener {
    if(getFragmentManager().getBackStackEntryCount() > 0) {
        getFragmentManager().popBackStack()
    }
    else {
        super.onBackPressed()
    }
    finish()
}*/