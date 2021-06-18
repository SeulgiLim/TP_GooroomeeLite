package kr.co.gooroomeelite.adapter

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ItemSubjectBinding
import kr.co.gooroomeelite.entity.Subject

class SubjectAdapter(
    private var subjects : List<DocumentSnapshot>,
    private val onClickStartBtn : (subject:DocumentSnapshot) -> Unit,
    private val onClickDeleteBtn : (subject:DocumentSnapshot) -> Unit,
    private val context: Context
    ) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(val binding:ItemSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject,parent,false)

        return SubjectViewHolder(ItemSubjectBinding.bind(view))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.binding.subjectColor.background.colorFilter = BlendModeColorFilter(Color.parseColor(subject["color"] as String), BlendMode.SRC_ATOP)
        holder.binding.subjectColor.setOnLongClickListener {
            MaterialColorPickerDialog.Builder(context)
                .setTitle("과목 색상 변경")
                .setColorShape(ColorShape.CIRCLE)
                .setColorSwatch(ColorSwatch._300)
                .setDefaultColor(R.color.white)
                .setColorListener { color, colorHex ->
                    it.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorHex), BlendMode.SRC_ATOP)
                    FirebaseFirestore.getInstance().collection("subject").document(subject.id).update("color", colorHex)
                }
                .show()
            true
        }
        holder.binding.subjectTitle.text = subject["name"] as String
        holder.binding.startBtn.setOnClickListener {
            onClickStartBtn(subject)
        }
        holder.binding.deleteBtn.setOnClickListener {
            onClickDeleteBtn(subject)
        }
        val studytime = subject["studytime"] as Long?
        studytime?.let {
            holder.binding.subjectStudytime.text = "${studytime / 60}시간 ${studytime % 60}분"
        }
    }

    fun setData(item : List<DocumentSnapshot>) {
        subjects = item
        notifyDataSetChanged()
    }

    override fun getItemCount() = subjects.size
}