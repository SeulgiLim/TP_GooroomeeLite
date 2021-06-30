package kr.co.gooroomeelite.views.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import kr.co.gooroomeelite.databinding.ActivityDeleteSubjectDialogBinding
import kr.co.gooroomeelite.views.home.SubjectFragment

class DeleteSubjectDialog(private val owner:SubjectFragment) : Dialog(owner.requireContext()) {
    private lateinit var binding: ActivityDeleteSubjectDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteSubjectDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.okBtn.setOnClickListener {
            owner.delete()
            dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}