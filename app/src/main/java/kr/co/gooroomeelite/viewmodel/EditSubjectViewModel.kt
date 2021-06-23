package kr.co.gooroomeelite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser
import java.util.LinkedList

class EditSubjectViewModel : ViewModel() {
    val db: FirebaseFirestore
    val subjectList = MutableLiveData<LinkedList<DocumentSnapshot>>()
    private val deletedSubjects: MutableList<DocumentSnapshot>
    val uid: String

    init {
        db = FirebaseFirestore.getInstance()
        subjectList.value = LinkedList()
        deletedSubjects = mutableListOf()
        uid = currentUser()!!.uid
        fetchSubjectList()
    }

    private fun fetchSubjectList() {
        db.collection("subject")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val tmp = hashMapOf<String, DocumentSnapshot>()
                    var current: DocumentSnapshot? = null
                    docs.documents.forEach {
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
                } else {
                    subjectList.value = LinkedList()
                }
                // 최근 순 정렬
                // subjectList.value = value.documents.sortedBy { it.getDate("timestamp") }
            }
    }

    fun deleteSubject(item: DocumentSnapshot) {
        subjectList.value!!.remove(item)
        deletedSubjects.add(item)

//        if (subjectList.value!!.size == 1) {
//            db.collection("subject").document(item.id).delete()
//            return
//        }
//        val prevDocumentId = item["prevDocumentId"] as String?
//        val nextDocumentId = item["nextDocumentId"] as String?
//        if (prevDocumentId != null && nextDocumentId != null) {//중간 원소일 경우
//            db.collection("subejct").document(prevDocumentId)
//                .update(hashMapOf("nextDocumentId" to nextDocumentId) as Map<String, Any>)
//            db.collection("subejct").document(nextDocumentId)
//                .update(hashMapOf("prevDocumentId" to prevDocumentId) as Map<String, Any>)
//        } else if (prevDocumentId != null) {//맨 마지막 원소일 경우
//            db.collection("subejct").document(prevDocumentId)
//                .update(hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
//        } else {//맨 처음 원소일 경우
//            db.collection("subejct").document(nextDocumentId!!)
//                .update(hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
//        }
//        db.collection("subject").document(item.id).delete()
    }

    fun saveToFirebase() =
        //transaction은 읽기/쓰기, runBatch는 쓰기만
        db.runBatch { writeBatch ->
            deletedSubjects.forEach {
                writeBatch.delete(it.reference)
            }
            deletedSubjects.clear()
            val lastIndex = subjectList.value!!.size - 1
            subjectList.value!!.apply {
                if (this.isEmpty()) {
                    return@runBatch
                }
                if (lastIndex == 0) {
                    writeBatch.update(this[0].reference,
                        hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
                    writeBatch.update(this[0].reference,
                        hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
                    return@runBatch
                }
            }
            subjectList.value!!.forEachIndexed { i, current ->
                when (i) {
                    0 -> {
                        current.apply {
                            if (this["prevDocumentId"] as String? != null) {
                                writeBatch.update(this.reference,
                                    hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
                            }
                            val next = subjectList.value!![i + 1]
                            if (this["nextDocumentId"] as String? != next.id) {
                                writeBatch.update(this.reference,
                                    hashMapOf("nextDocumentId" to next.id) as Map<String, Any>)
                            }
                        }
                    }
                    lastIndex -> {
                        current.apply {
                            val prev = subjectList.value!![i - 1]
                            if (this["prevDocumentId"] as String? != prev.id) {
                                writeBatch.update(this.reference,
                                    hashMapOf("prevDocumentId" to prev.id) as Map<String, Any>)
                            }

                            if (this["nextDocumentId"] as String? != null) {
                                writeBatch.update(this.reference,
                                    hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
                            }
                        }
                    }
                    else -> {
                        current.apply {
                            val prev = subjectList.value!![i - 1]
                            if (this["prevDocumentId"] as String? != prev.id) {
                                writeBatch.update(this.reference,
                                    hashMapOf("prevDocumentId" to prev.id) as Map<String, Any>)
                            }

                            val next = subjectList.value!![i + 1]
                            if (this["nextDocumentId"] as String? != next.id) {
                                writeBatch.update(this.reference,
                                    hashMapOf("nextDocumentId" to next.id) as Map<String, Any>)
                            }
                        }
                    }
                }
            }
        }
}
