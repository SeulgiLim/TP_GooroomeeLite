package kr.co.gooroomeelite.views.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Window
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityPomoFocusDialogBinding
import kr.co.gooroomeelite.databinding.ActivityStudyTimerBinding



class PomoFocusDialog : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }


}

/*class PomoFocusDialog(private val owner:TimersettingActivity) : Dialog(owner.TimersettingActivity) { //

    private lateinit var binding: ActivityPomoFocusDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_pomo_focus_dialog)
        binding = ActivityPomoFocusDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.hourPicker.displayedValues = arrayOf(
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
        )

        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 5//6개
        binding.minutePicker.displayedValues = arrayOf("00", "10", "20", "30", "40", "50")

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        /*
        // 허허 owner를 못찾는구나...
        binding.okBtn.setOnClickListener {
            owner.setStudyTimeCallback(binding.hourPicker.value * 60 + binding.minutePicker.value * 10)
            dismiss()
    }*/
    }
}*/