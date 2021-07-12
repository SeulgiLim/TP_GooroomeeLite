package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityStudyEndBinding

class StudyEndActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStudyEndBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}