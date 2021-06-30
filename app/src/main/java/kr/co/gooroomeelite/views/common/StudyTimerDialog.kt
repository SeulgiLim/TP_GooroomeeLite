package kr.co.gooroomeelite.views.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import kr.co.gooroomeelite.databinding.ActivityStudyTimerBinding
import kr.co.gooroomeelite.views.home.HomeFragment

class StudyTimerDialog(private val owner:HomeFragment) : Dialog(owner.requireContext()) {
    private lateinit var binding: ActivityStudyTimerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = ActivityStudyTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.hourPicker.displayedValues = arrayOf("00","01","02","03","04","05","06","07","08","09","10","11","12",
                                                                    "13","14","15","16","17","18","19","20","21","22","23")

        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 5//6개
        binding.minutePicker.displayedValues = arrayOf("00","10","20","30","40","50")

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.okBtn.setOnClickListener {
            owner.setStudyTimeCallback(binding.hourPicker.value * 60 + binding.minutePicker.value * 10)
            dismiss()
        }
    }
}