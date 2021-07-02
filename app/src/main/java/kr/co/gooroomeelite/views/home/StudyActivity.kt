package kr.co.gooroomeelite.views.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_withdrawal.*
import kotlinx.android.synthetic.main.fragment_pomodoro.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityTermsOfServiceBinding
import kr.co.gooroomeelite.databinding.FragmentHomeBinding
import kr.co.gooroomeelite.entity.Subject




//전역변수 선언
private lateinit var subject : Subject
private lateinit var documentId : String
// private lateinit var nowstudytime : Int

class StudyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)


        // StudyActivity에서 HomeFragment로 이동
        // 한번 클릭하면 왜 화면이 2번나오지? 미쳤나 ㅠㅠㅠ
        btn_back.setOnClickListener {
            onBackPressed()
        }

        // 타이머설정 버튼 클릭시 타이머 설정 화면으로 이동
        btn_timesetting.setOnClickListener {
            val intent = Intent(this, TimersettingActivity::class.java)
            startActivity(intent)
        }


        // HomeFragment에서 과목, 문서 ID 받아오기, 전역변수 지정하여, 과목 정보 받기 * 전역변수 : 메서드 밖, 즉 클래스 안에서 선언된 변수를 가져다 쓰는 변수
       val subject = intent.getSerializableExtra("subject") as Subject
       val documentId = intent.getSerializableExtra("documentId") as String  // 서버에 시간 데이터 저장시 필요함 (저장 방법 결정 필요)
        // nowstudytime = subject.studytime


        //StudyActivity -> TimerFragment로 데이터 넘기기
        val bundle = Bundle()
        bundle.putSerializable("subject", subject)
        bundle.putString("documentId", documentId)

        //val 타이머 프래그먼트 = 타이머프래그먼트()
        //타이머프래그먼트.arguments = bundle



        // 스탑워치 버튼


        // 과목변경 버튼
        btn_sc.setOnClickListener {
            val StopwatchFragment : StopwatchFragment = StopwatchFragment()
            val fragmentManager : FragmentManager = supportFragmentManager


            val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
            fragmentTransaction.replace(R.id.container, StopwatchFragment)            // 할 일
            fragmentTransaction.commit()                                        // 끝

            Log.d("aaa 3",subject.toString())

        }

        // 화이트 노이즈 버튼
        btn_wn.setOnClickListener {

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
        }

    }


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
}