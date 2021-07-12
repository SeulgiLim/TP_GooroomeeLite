package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityStudyEndBinding
import kr.co.gooroomeelite.viewmodel.StudyEndViewModel
import kr.co.gooroomeelite.viewmodel.SubjectViewModel

class StudyEndActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStudyEndBinding
    private val viewModel: StudyEndViewModel by viewModels()
    private val todayStudyTime = MutableLiveData<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}