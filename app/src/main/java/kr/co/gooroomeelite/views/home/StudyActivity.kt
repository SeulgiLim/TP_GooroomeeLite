package kr.co.gooroomeelite.views.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_withdrawal.*
import kotlinx.android.synthetic.main.fragment_pomodoro.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.entity.Subject

//전역변수 선언
// private lateinit var subject : Subject
// private lateinit var documentId : String
// private lateinit var 오늘공부시간 : Int

class StudyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)


        // HomeFragment에서 과목, 문서 ID 받아오기, 전역변수 지정하여, 과목 정보 받기 * 전역변수 :  메서드 밖, 즉 클래스 안에서 선언된 변수를 가져다 쓰는 변수
        // val subject = intent.getSerializableExtra("subject") as Subject
        // val documentId = intent.getSerializableExtra("documentId") as String  // 서버에 시간 데이터 저장시 필요함 (저장 방법 결정 필요)
        // 오늘공부시간 = subject.studytime

        // Log.d("subject", subject.toString())
        // Log.d("documentId", documentId)


        // 스탑워치 버튼


        // 스탑워치 화면 전환 버튼 -> 과목변경 버튼으로 변경예정
        btnST.setOnClickListener {
            val StopwatchFragment : StopwatchFragment = StopwatchFragment()
            val fragmentManager : FragmentManager = supportFragmentManager


            val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
            fragmentTransaction.replace(R.id.container, StopwatchFragment)            // 할 일
            // addToBackStack(null)
            fragmentTransaction.commit()                                        // 끝

            // textview로 데이터를 보낸다고?
            // 공부시간 데이터 보내기 (putExtra 넣기)
           // val intent = Intent(this@StudyActivity, HomeFragment::class.java)
            // intent.putExtra("pauseOffset", pauseOffset.text.toString())
           // startActivity(intent)


        }

        // 뽀모도로 화면 전환 버튼 -> 화이트 노이즈 버튼로 변경예정
        btnPM.setOnClickListener {

            // 공부시간 데이터 보내기 (intent (putExtra 넣기) : Activity/Fragment -> Activity)

            // 공부시간 데이터 보내기 (Bundle : Activity/Fragment -> Fragment)
            // 1) 이동할 Fragment 객체 생성
            // 2) Bundle 객체 생성 및 데이터 저장 -> bundle.putXXXX (name, value)
            // 3) Fragment 객체.argument = Bundle 객체

            val HomeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putInt("pauseOffset",0)
            HomeFragment.arguments = bundle     // fragment의 arguments에 담은 bundle을 넘겨줌

           // StudyActivity.supportFragmentManger!!.beginTransaction()
            //        .replace(R.id.fragment_phone, HomeFragment)
            //        .commit()

            // val intent = Intent(this@StudyActivity, HomeFragment::class.java)
            // intent.putExtra("text_view_pomodoro", text_view_pomodoro?.text?.toString())
            //startActivity(intent)

            val PomodoroFragment : PomodoroFragment = PomodoroFragment()
            val fragmentManager : FragmentManager = supportFragmentManager


            val fragmentTransaction = fragmentManager.beginTransaction()        // 시작
            fragmentTransaction.replace(R.id.container, PomodoroFragment)            // 할 일
            fragmentTransaction.commit()                                        // 끝
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