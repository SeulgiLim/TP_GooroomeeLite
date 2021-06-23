package kr.co.gooroomeelite.views.home

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.FragmentSubjectBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import java.time.LocalDateTime

class SubjectFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentSubjectBinding
    private var selectedColor = ""
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSubjectBinding.inflate(inflater, container, false)

        binding.colorSelector.setOnClickListener {
            MaterialColorPickerDialog.Builder(requireContext())
                .setTitle("과목 색상 선택")
                .setColorShape(ColorShape.CIRCLE)
                .setColorSwatch(ColorSwatch._300)
                .setDefaultColor(R.color.white)
                .setColorListener { color, colorHex ->
                    it.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorHex), BlendMode.SRC_ATOP)
                    selectedColor = colorHex
                }
                .show()
        }

        binding.enrollBtn.setOnClickListener {
            if(binding.subjectName.text.isNotBlank() and selectedColor.isNotBlank()) {
                setFragmentResult("subject", bundleOf("subject" to Subject(getUid(), binding.subjectName.text.toString(), selectedColor)))
                dismiss()
            } else {
                Toast.makeText(requireContext(),"과목 이름과 색상을 입력해 주세요.",Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
}