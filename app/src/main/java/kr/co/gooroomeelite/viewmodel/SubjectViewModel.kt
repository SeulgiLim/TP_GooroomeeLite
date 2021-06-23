package kr.co.gooroomeelite.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser
import java.util.LinkedList

class SubjectViewModel : ViewModel() {
    val db: FirebaseFirestore
    val subjectList = MutableLiveData<LinkedList<DocumentSnapshot>>()
    val uid: String

    init {
        db = FirebaseFirestore.getInstance()
        subjectList.value = LinkedList()
        uid = currentUser()!!.uid
        fetchSubjectList()
    }

    private fun fetchSubjectList() {
        db.collection("subject")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (value != null) {
                    if(value.documents.isEmpty()) {
                        subjectList.value = LinkedList()
                        return@addSnapshotListener
                    }
                    val tmp = hashMapOf<String, DocumentSnapshot>()
                    var current: DocumentSnapshot? = null
                    value.documents.forEach {
                        if (it["prevDocumentId"] as String? == null) {
                            current = it
                        }
                        tmp.put(it.id, it)
                    }
                    val tmp2 = LinkedList<DocumentSnapshot>()
                    while (true) {
                        tmp2.add(current!!)
                        val next = current!!["nextDocumentId"] as String?
                        if (next != null) {
                            current = tmp[next]
                        } else {
                            break
                        }
                    }
                    subjectList.value = tmp2
                }
                // 최근 순 정렬
                // subjectList.value = value.documents.sortedBy { it.getDate("timestamp") }
            }
    }

    fun addSubject(item: Subject) {
        var prevSubject: DocumentSnapshot? = null
        if (subjectList.value!!.size > 0) {
            prevSubject = subjectList.value!!.last
        }
        db.collection("subject").add(item)
            //아래의 addOnSuccessListener보다 위의 addSnapshotListener가 먼저 호출된다.
            .addOnSuccessListener { newSubject ->
                subjectList.value!!.apply {
                    if (prevSubject != null) {
                        db.collection("subject").document(prevSubject.id).update(
                            hashMapOf("nextDocumentId" to newSubject.id) as Map<String, Any>
                        )
                        db.collection("subject").document(newSubject.id).update(
                            hashMapOf("prevDocumentId" to prevSubject.id) as Map<String, Any>
                        )
                    }
                }
            }
    }

//    fun deleteSubject(item: DocumentSnapshot) {
//        deletedSubjects.add(item)
//
////        if (subjectList.value!!.size == 1) {
////            db.collection("subject").document(item.id).delete()
////            return
////        }
////        val prevDocumentId = item["prevDocumentId"] as String?
////        val nextDocumentId = item["nextDocumentId"] as String?
////        if (prevDocumentId != null && nextDocumentId != null) {//중간 원소일 경우
////            db.collection("subejct").document(prevDocumentId)
////                .update(hashMapOf("nextDocumentId" to nextDocumentId) as Map<String, Any>)
////            db.collection("subejct").document(nextDocumentId)
////                .update(hashMapOf("prevDocumentId" to prevDocumentId) as Map<String, Any>)
////        } else if (prevDocumentId != null) {//맨 마지막 원소일 경우
////            db.collection("subejct").document(prevDocumentId)
////                .update(hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
////        } else {//맨 처음 원소일 경우
////            db.collection("subejct").document(nextDocumentId!!)
////                .update(hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
////        }
////        db.collection("subject").document(item.id).delete()
//    }

//    fun saveToFirebase() =
//        //transaction은 읽기/쓰기, runBatch는 쓰기만
//        db.runBatch { writeBatch ->
//            deletedSubjects.forEach {
//                writeBatch.delete(it.reference)
//            }
//            deletedSubjects.clear()
//            val lastIndex = subjectList.value!!.size - 1
//            subjectList.value!!.forEachIndexed { i, doc ->
//                when(i) {
//                    0 -> {
//                        writeBatch.update(doc.reference, hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
//
//                    }
//                    lastIndex -> {
//
//                    }
//                    else -> {
//
//                    }
//                }
//                if(i == 0) {
//
//                }
//            }
//            while(true) {
//                val current = iterator.next()
//                Log.d("save",current.data.toString())
//                if(iterator.hasPrevious()) {
//                    val prev = subjectList.value!![iterator.previousIndex()]
//                    if(prev["nextDocumentId"] as String? != current.id) {
//                        writeBatch.update(prev.reference, hashMapOf("nextDocumentId" to current.id) as Map<String, Any>)
//                    }
//                } else {//맨 첫번째 과목일 경우
//
//                }
//
//                if(iterator.hasNext()) {
//                    val next = subjectList.value!![iterator.nextIndex()]
//                    if(next["prevDocumentId"] as String? != current.id) {
//                        writeBatch.update(next.reference, hashMapOf("prevDocumentId" to current.id) as Map<String, Any>)
//                    }
//                } else {
//                    writeBatch.update(current.reference, hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
//                    break
//                }
//            }
//        }
}
