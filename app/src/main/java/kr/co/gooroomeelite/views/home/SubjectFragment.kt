package kr.co.gooroomeelite.views.home

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.customview.RoundedSquare
import kr.co.gooroomeelite.databinding.FragmentSubjectBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import kr.co.gooroomeelite.views.common.DeleteSubjectDialog

class SubjectFragment : BottomSheetDialogFragment() {
    private val canSave = MutableLiveData<Boolean>()
    private lateinit var binding: FragmentSubjectBinding
    private var selectedPosition = -1
    private val maxLength = 10
    private var isEditing = false
    private var target : Subject? = null
    private var targetPosition : Int? = null
    interface onDataPassListener {
        fun delete(position: Int)
        fun edit(position: Int, subjectName: String, color: String)
    }
    private lateinit var dataPassListener : onDataPassListener //lateinit를 통해 나중에 초기화
    private val colorList = listOf(
            "#F2A6A0", "#F5BF9E", "#F9D69D", "#FBE0C2", "#FCE8E1",
            "#FCEAA1", "#EEE9AB", "#DEE88E", "#CCE99E", "#D8EBD8",
            "#D2EBEE", "#E0F1F4", "#C8DCEB", "#AFBEC8", "#CFD7DB"
    )
    private val colorPicker by lazy {
        listOf(binding.color1, binding.color2, binding.color3, binding.color4, binding.color5,
                binding.color6, binding.color7, binding.color8, binding.color9, binding.color10,
                binding.color11, binding.color12, binding.color13, binding.color14, binding.color15)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if(activity is EditSubjectsActivity) {
            dataPassListener = activity //dataPassListener를 acitivity로 형 변환
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        canSave.value = false
        binding = FragmentSubjectBinding.inflate(inflater, container, false)

        binding.enrollBtn.setOnClickListener {
            if(isEditing) {
                dataPassListener.edit(targetPosition!!, subjectName = binding.subjectName.text.toString(), color = colorList[selectedPosition])
            } else {
                setFragmentResult("subject", bundleOf("subject" to Subject(getUid(), binding.subjectName.text.toString(), colorList[selectedPosition])))
            }
            dismiss()
        }

        binding.subjectName.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                canSave.value = !s.isNullOrEmpty() and (selectedPosition != -1)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.subjectName.apply {
                    if (this.isFocusable && s.toString() != "") {
                        val string: String = s.toString()
                        val len = string.length
                        if (len > maxLength) {
                            this.setText(string.substring(0, maxLength))
                            this.setSelection(maxLength)
                        } else {
                            binding.textCount.text = "$len / $maxLength"
                            canSave.value = (selectedPosition != -1)
                        }
                    } else {
                        binding.textCount.text = "0 / $maxLength"
                        canSave.value = false
                    }
                }
            }
        })

        binding.deleteBtn.setOnClickListener {
            val dialog = DeleteSubjectDialog(this)
            dialog.show()
        }
        binding.deleteBtn.visibility =  View.GONE

        initColorPicker()

        arguments?.let {
            target = it.getSerializable("subject") as Subject
            targetPosition = it.getInt("position")
        }
        target?.let {
            isEditing = true
            canSave.value = true
            colorList.forEachIndexed { index, color ->
                if(color == it.color) {
                    selectedPosition = index
                    colorPicker[selectedPosition].children.last().visibility = View.VISIBLE
                    return@forEachIndexed
                }
            }
            binding.subjectName.setText(it.name)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        canSave.observe(viewLifecycleOwner) {
            binding.enrollBtn.apply {
                isClickable = it
                setTextColor(if(it) Color.parseColor("#FFFFFF") else Color.parseColor("#999999"))
                setBackgroundColor(if(it) Color.parseColor("#51A9FE") else Color.parseColor("#EEEEEE"))
            }
        }

        if(isEditing) {
            binding.deleteBtn.visibility = View.VISIBLE
            binding.title.text = "과목 편집"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    private fun initColorPicker() {
        colorPicker.forEachIndexed { position, square ->
            square.children.apply {
                (first() as RoundedSquare).paint.color = Color.parseColor(colorList[position])
                (first() as RoundedSquare).setOnClickListener {
                    val prevPosition = selectedPosition
                    if(prevPosition != -1) {
                        colorPicker[prevPosition].children.last().visibility = View.GONE
                    }
                    selectedPosition = position
                    colorPicker[selectedPosition].children.last().visibility = View.VISIBLE
                    canSave.value = !binding.subjectName.text.isNullOrEmpty()
                }
                last().visibility = View.GONE
            }
        }
    }

    fun delete() {
        dataPassListener.delete(targetPosition!!)
        dismiss()
    }
}

