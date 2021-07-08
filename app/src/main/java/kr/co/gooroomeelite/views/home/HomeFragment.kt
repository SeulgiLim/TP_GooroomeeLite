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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_pomodoro.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
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
            })

        /*onClickStartBtn = { subject ->
            val intent = Intent(context,StudyActivity::class.java)
            // intent.putExtra("key", "value")
            // setResult(RESULT.OK, intent)
            startActivity(intent)
            // finish()
        })*/
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
        getUserInfo()
        childFragmentManager.setFragmentResultListener("subject",
            viewLifecycleOwner) { resultKey, bundle ->
            if(viewModel.subjectList.value!!.size <= 20) {
                Toast.makeText(mainActivityContext, "과목을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                val subject = bundle.getSerializable("subject") as Subject
                viewModel.addSubject(subject)
            } else {
                Toast.makeText(mainActivityContext, "과목은 최대 20개 까지만 등록할 수 있습니다.", Toast.LENGTH_SHORT).show()
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

//        val ingImg = binding.ing.layoutParams as ConstraintLayout.LayoutParams
//        ingImg.horizontalBias = binding.seekBar.progress.toFloat() / 10

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

        todayStudyTime.value = 0

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.subjectList.observe(viewLifecycleOwner) {
            subjectAdapter.setData(it)
            binding.subjectExample.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        myStudyGoal.observe(viewLifecycleOwner) {
            binding.studytime.text =
                "%02d".format(myStudyGoal.value?.div(60)) + "시간 " + "%02d".format(myStudyGoal.value?.rem(
                    60)) + "분"
        }

        binding.subjectEdit.setOnClickListener {
            startActivity(Intent(mainActivityContext, EditSubjectsActivity::class.java))
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode != Activity.RESULT_OK) {
//            when(requestCode) {
//                RC_ENROLL_STUDY_TIME -> { activity?.finish() }
//            }
//            return
//        }
//    }

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
                            Toast.makeText(mainActivityContext,
                                "목표 공부 시간을 수정하였습니다.",
                                Toast.LENGTH_SHORT)
                                .show()
                            myStudyGoal.value = studyTime
                        }
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(LoginUtils.getUid()!!)
                        .set(hashMapOf("studyTime" to studyTime))
                        .addOnSuccessListener {
                            Toast.makeText(mainActivityContext,
                                "목표 공부 시간을 설정하였습니다.",
                                Toast.LENGTH_SHORT)
                                .show()
                            myStudyGoal.value = studyTime
                        }
                }
            }
    }

    private fun getUserInfo() {
        FirebaseFirestore.getInstance().collection("users").document(getUid()!!).get()
            .addOnSuccessListener {
                if(it["studyTime"] == null) {
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
}