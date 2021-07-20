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
import kr.co.gooroomeelite.databinding.ItemSubjectDailyBinding
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
class WeeklySubjectAdapter(
    private var subjects: List<DocumentSnapshot>
) : RecyclerView.Adapter<WeeklySubjectAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(val binding: ItemSubjectDailyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_subject_daily, parent, false)

        return SubjectViewHolder(ItemSubjectDailyBinding.bind(view))
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.binding.subjectColor.paint.color = Color.parseColor(subject["color"] as String)
        holder.binding.subjectTitle.text = subject["name"] as String
        val plus : Int = 0
        val studytime = plus.toLong() + subject["studytime"] as Long
        holder.binding.subjectStudytime.text = "${studytime / 60}시간 ${studytime % 60}분"
    }

    override fun getItemCount() = subjects.size

    fun setData(item: LinkedList<DocumentSnapshot>) {
        subjects = item
        notifyDataSetChanged()
    }
}
