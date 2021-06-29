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
import kr.co.gooroomeelite.databinding.ItemSubjectBinding
import java.util.*

class SubjectAdapter(
    private var subjects : List<DocumentSnapshot>,
    private val onClickStartBtn : (subject:DocumentSnapshot) -> Unit,
    ) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(val binding:ItemSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject,parent,false)

        return SubjectViewHolder(ItemSubjectBinding.bind(view))
    }

    //하나씩 가져오면 된다.~
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.binding.subjectColor.background.colorFilter = BlendModeColorFilter(Color.parseColor(subject["color"] as String), BlendMode.SRC_ATOP)
        holder.binding.subjectTitle.text = subject["name"] as String
        holder.binding.startBtn.setOnClickListener {
            onClickStartBtn(subject)
        }
        val studytime = subject["studytime"] as Long
        holder.binding.subjectStudytime.text = "${studytime / 60}시간 ${studytime % 60}분"
    }

    fun setData(item : LinkedList<DocumentSnapshot>) {
        subjects = item
        notifyDataSetChanged()
    }

    override fun getItemCount() = subjects.size
}