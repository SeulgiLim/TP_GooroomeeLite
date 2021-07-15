package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityAsmrBinding
import kr.co.gooroomeelite.databinding.ActivityStudyBinding

class AsmrActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAsmrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asmr)

        binding = ActivityAsmrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 (ASMRActivity -> StudyActivity 화면 이동)
        binding.btnBack.setOnClickListener{
            finish()
        }

    }
}