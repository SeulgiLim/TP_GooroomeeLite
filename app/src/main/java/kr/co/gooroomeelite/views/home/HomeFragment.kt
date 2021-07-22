package kr.co.gooroomeelite.views.home

import android.app.Activity
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_pomodoro.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.SubjectAdapter
import kr.co.gooroomeelite.databinding.FragmentHomeBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import kr.co.gooroomeelite.utils.RC_START_STUDY
import kr.co.gooroomeelite.viewmodel.SubjectViewModel
import kr.co.gooroomeelite.views.common.StudyTimerDialog
import kr.co.gooroomeelite.views.login.LoginActivity
import splitties.resources.int
import splitties.systemservices.windowManager
import java.lang.NullPointerException
import java.lang.NumberFormatException
import kotlin.math.round
import kotlin.math.roundToInt


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: SubjectViewModel by viewModels()
    private val subjectAdapter: SubjectAdapter by lazy {
        SubjectAdapter(emptyList(),
            onClickStartBtn = { subject ->
                val intent = Intent(mainActivityContext, StudyActivity::class.java)
                intent.putExtra("subject", subject.toObject(Subject::class.java))
                intent.putExtra("documentId", subject.id)
                startActivityForResult(intent, RC_START_STUDY)

                Log.d("aaa1",subject.toString())
                startActivity(intent)                       // 새로운 Activity를 화면에 띄울 때
            })

    }
    private val mainActivityContext by lazy {
        requireContext()
    }
    private val myStudyGoal = MutableLiveData<Int>()
    private val todayStudyTime = MutableLiveData<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.home = this
        getUserInfo()
        childFragmentManager.setFragmentResultListener(
            "subject",
            viewLifecycleOwner
        ) { resultKey, bundle ->
            if (viewModel.subjectList.value!!.size <= 20) {
                Toast.makeText(mainActivityContext, "과목을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                 // 여기서 문자열을 사용하지만 번들에 넣을 수있는 모든 유형이 지원된다.
                val subject = bundle.getSerializable("subject") as Subject //bundle로 받기
                viewModel.addSubject(subject)
            } else {
                Toast.makeText(
                    mainActivityContext,
                    "과목은 최대 20개 까지만 등록할 수 있습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tmp.setOnLongClickListener {
            if (LoginUtils.isLogin()) {
                LoginUtils.signOut(mainActivityContext)
                Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(mainActivityContext, LoginActivity::class.java))
                activity?.finish()
            }
            true
        }

        binding.subjectEnroll.setOnClickListener {
            val bottomSheet = SubjectFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                mainActivityContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = subjectAdapter
        }

        binding.setStudytimeBtn2.setOnClickListener {
            StudyTimerDialog(this).show()
        }

        //Seekbar, 오늘 공부시간 설정
        getTotalStudy()
        seekbar()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.subjectList.observe(viewLifecycleOwner) {
            subjectAdapter.setData(it)
            binding.subjectExample.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        myStudyGoal.observe(viewLifecycleOwner) {
            binding.studytime.text =
                "%02d".format(myStudyGoal.value?.div(60)) + "시간 " + "%02d".format(
                    myStudyGoal.value?.rem(
                        60
                    )
                ) + "분"
        }
        binding.subjectEdit.setOnClickListener {
            startActivity(Intent(mainActivityContext, EditSubjectsActivity::class.java))
        }
        todayStudyTime.observe(viewLifecycleOwner) {
            binding.hour.text =
                "%02d".format(todayStudyTime.value?.div(60))
            binding.minute.text =
                "%02d".format(todayStudyTime.value?.rem(60))
        }

    }

    fun setStudyTimeCallback(studyTime: Int) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(LoginUtils.getUid()!!)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(LoginUtils.getUid()!!)
                        .update(hashMapOf("studyTime" to studyTime) as Map<String, Any>) // 업데이트 부분
                        .addOnSuccessListener {
                            Toast.makeText(
                                mainActivityContext,
                                "목표 공부 시간을 수정하였습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            myStudyGoal.value = studyTime
                        }
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(LoginUtils.getUid()!!)
                        .set(hashMapOf("studyTime" to studyTime))
                        .addOnSuccessListener {
                            Toast.makeText(
                                mainActivityContext,
                                "목표 공부 시간을 설정하였습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            myStudyGoal.value = studyTime
                        }
                }
            }
    }

    private fun getUserInfo() {
        FirebaseFirestore.getInstance().collection("users").document(getUid()!!).get()
            .addOnSuccessListener {
                if (it["studyTime"] == null) {
                    myStudyGoal.value = -1
                    return@addOnSuccessListener
                }
                myStudyGoal.value = it["studyTime"].toString().toInt()
                binding.nickname.text = "반가워요, ${it["nickname"]}님"
            }
            .addOnFailureListener {
                Toast.makeText(mainActivityContext, it.toString(), Toast.LENGTH_SHORT).show()
                myStudyGoal.value = -1
            }
    }

    fun getTotalStudy() {
        FirebaseFirestore.getInstance()
            .collection("subject")
            .whereEqualTo("uid", getUid())
            .get()
            .addOnSuccessListener {
                val subject = it.toObjects(Subject::class.java)
                var studytimetodaylist = mutableListOf<Int>()
                for (i in 0..subject.size - 1) {
                    studytimetodaylist.add(subject[i].studytime)
                }
                todayStudyTime.value = studytimetodaylist.sum()
                FirebaseFirestore.
                getInstance()
                    .collection("users")
                    .document(getUid()!!)
                    .update("todaystudytime",todayStudyTime.value)
            }
        Log.e("TEST,","111")
    }

    fun seekbar() : Int {
        myStudyGoal.observe(viewLifecycleOwner) {
            todayStudyTime.observe(viewLifecycleOwner) {
                if (myStudyGoal.value != 0) {
                    val percent: Float =
                        ((todayStudyTime.value!! * 100 / (myStudyGoal.value!!)) / 100.toFloat()) * 100
                    binding.seekBar.progress = percent.toInt()
                }
                else{
                    binding.seekBar.progress = 100
                }
            }
        }
        return Log.e("TEST,","222")
    }
}
