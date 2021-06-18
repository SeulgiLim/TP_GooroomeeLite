package kr.co.gooroomeelite.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser

class SubjectViewModel : ViewModel() {
    val db : FirebaseFirestore
    val subjectList = MutableLiveData<List<DocumentSnapshot>>()
    val uid : String

    init {
        db = FirebaseFirestore.getInstance()
        subjectList.value = emptyList()
        uid = currentUser()!!.uid
        fetchSubjectList()
    }

    private fun fetchSubjectList() {
        db.collection("subject")
            .whereEqualTo("uid", uid)
            .addSnapshotListener{ value , error ->
                if(error != null) {
                    return@addSnapshotListener
                }

                if(value != null) {
                    subjectList.value = value.documents.sortedBy { it.getDate("timestamp") }
//                    (subjectList.value as MutableList<DocumentSnapshot>).forEach {
//                        Log.d("subjectlist","${it["name"]} : ${it.getDate("timestamp")}")
//                    }
                }
            }
    }

    fun addSubject(item: Subject) {
        db.collection("subject").add(item)
    }

    fun deleteSubject(item: DocumentSnapshot) {
        db.collection("subject").document(item.id).delete()
    }
}
