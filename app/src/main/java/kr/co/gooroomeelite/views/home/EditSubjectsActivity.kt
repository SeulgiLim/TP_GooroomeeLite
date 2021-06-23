package kr.co.gooroomeelite.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.gooroomeelite.adapter.EditSubjectAdapter
import kr.co.gooroomeelite.databinding.ActivityEditSubjectsBinding
import kr.co.gooroomeelite.viewmodel.EditSubjectViewModel
import java.util.*

class EditSubjectsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditSubjectsBinding
    private val isLoading = MutableLiveData<Boolean>()
    private val viewModel : EditSubjectViewModel by viewModels()
    private val editSubjectAdapter: EditSubjectAdapter by lazy {
        EditSubjectAdapter(emptyList(),
            onClickUpBtn = { position ->
                if(position > 0) {
                    viewModel.subjectList.value?.let {
                        Collections.swap(it, position, position - 1)
                        this.editSubjectAdapter.notifyDataSetChanged()
                    }
                }
            },
            onClickDownBtn = { position ->
                if(position < viewModel.subjectList.value!!.size - 1) {
                    viewModel.subjectList.value?.let {
                        Collections.swap(it, position, position + 1)
                        this.editSubjectAdapter.notifyDataSetChanged()
                    }
                }
            },
            onClickMoreBtn = { subject ->
                viewModel.deleteSubject(subject)
                this.editSubjectAdapter.notifyDataSetChanged()
            }
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSubjectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLoading.value = false
        binding.cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.saveBtn.setOnClickListener {
            isLoading.value = true
            viewModel.saveToFirebase()
                .addOnSuccessListener {
                    isLoading.value = false
                    finish()
                }
                .addOnFailureListener {
                    isLoading.value = false
                    Toast.makeText(this,"과목 편집에 실패하였습니다. 에러 : $it",Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@EditSubjectsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = editSubjectAdapter
        }

        viewModel.subjectList.observe(this) {
            editSubjectAdapter.setData(it)
            binding.noEditSubejctImg.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        }

        isLoading.observe(this) {
            binding.progressBar.visibility = if(it) View.VISIBLE else View.GONE
        }
    }
}