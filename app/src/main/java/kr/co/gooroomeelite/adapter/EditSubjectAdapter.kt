package kr.co.gooroomeelite.adapter

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ItemEditSubjectBinding

class EditSubjectAdapter(
    private var editSubjects : List<DocumentSnapshot>,
    private val onClickUpBtn : (position:Int) -> Unit,
    private val onClickDownBtn : (position:Int) -> Unit,
    private val onClickMoreBtn : (subject:DocumentSnapshot) -> Unit
    ) : RecyclerView.Adapter<EditSubjectAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(val binding:ItemEditSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edit_subject, parent,false)

        return SubjectViewHolder(ItemEditSubjectBinding.bind(view))
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val editSubject = editSubjects[position]
        holder.binding.subjectColor.background.colorFilter = BlendModeColorFilter(Color.parseColor(editSubject["color"] as String), BlendMode.SRC_ATOP)
        holder.binding.subjectTitle.text = editSubject["name"] as String
        holder.binding.moveUpBtn.setOnClickListener {
            onClickUpBtn(position)
        }
        holder.binding.moveDownBtn.setOnClickListener {
            onClickDownBtn(position)
        }
        holder.binding.more.setOnClickListener {
            onClickMoreBtn(editSubject)
        }
    }

    fun setData(item : List<DocumentSnapshot>) {
        editSubjects = item
        notifyDataSetChanged()
    }

    override fun getItemCount() = editSubjects.size
}