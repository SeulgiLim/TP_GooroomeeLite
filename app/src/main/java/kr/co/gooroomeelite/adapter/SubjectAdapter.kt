package kr.co.gooroomeelite.adapter

import android.graphics.Color
import android.os.Build
import android.util.Log
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.binding.subjectColor.paint.color = Color.parseColor(subject["color"] as String)
        holder.binding.subjectTitle.text = subject["name"] as String
        holder.binding.startBtn.setOnClickListener {
            onClickStartBtn(subject)
        }
        val studytime = subject["studytime"] as Long
        holder.binding.subjectStudytime.text = "${studytime / 60}시간 ${studytime % 60}분"

    }

    fun setData(item : LinkedList<DocumentSnapshot>) {
        subjects = item
        //연결된 관찰자에게 기본 데이터가 변경되었으며 데이터 세트를 반영하는 모든보기가 자체적으로 새로 고쳐 져야 함을 알린다.
        notifyDataSetChanged()
    }

    override fun getItemCount() = subjects.size
}