package kr.co.gooroomeelite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.entity.EditSubject
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser
import java.util.LinkedList

class EditSubjectViewModel : ViewModel() {
    val db: FirebaseFirestore
    val subjectList = MutableLiveData<LinkedList<EditSubject>>()
    private val deletedSubjects: MutableList<DocumentSnapshot>
    private val editedSubjects: MutableList<EditSubject>
    val uid: String

    init {
        db = FirebaseFirestore.getInstance()
        subjectList.value = LinkedList()
        deletedSubjects = mutableListOf()
        editedSubjects = mutableListOf()
        uid = currentUser()!!.uid
        fetchSubjectList()
    }

    private fun fetchSubjectList() {
        db.collection("subject")
            .whereEqualTo("uid", uid) //계정 id
            .get() // 값이 변경시 바로 값이 변경된다.
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val tmp = hashMapOf<String, EditSubject>()
                    lateinit var start: EditSubject
                    docs.documents.forEach {
                        if (it["prevDocumentId"] as String? == null) {
                            start = EditSubject(it.toObject(Subject::class.java)!!, it)
                        }
                        tmp.put(it.id, EditSubject(it.toObject(Subject::class.java)!!, it))
                    }
                    val tmp2 = LinkedList<EditSubject>()
                    var current = start
                    while (true) {
                        tmp2.add(current)
                        val next = current.doc["nextDocumentId"] as String?
                        if (next != null) {
                            current = tmp[next]!!
                        } else {
                            break
                        }
                    }
                    tmp2.reverse()
                    subjectList.value = tmp2
                } else {
                    subjectList.value = LinkedList()
                }
                // 최근 순 정렬
                // subjectList.value = value.documents.sortedBy { it.getDate("timestamp") }
            }
    }

    fun deleteSubject(position: Int) {
        subjectList.value!!.apply {
            deletedSubjects.add(this[position].doc)
            removeAt(position)
        }

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

    //과목 수정
    fun editSubject(position: Int, subjectName: String, color: String) {
        subjectList.value!![position].apply {
            editedSubjects.add(this)
            subject.color = color
            subject.name = subjectName
        }
    }
    //완료 버튼 클릭 시 파이어베이스에 저장
    fun saveToFirebase() =
        //transaction은 읽기/쓰기, runBatch는 쓰기만
        db.runBatch { writeBatch ->
            deletedSubjects.forEach {
                writeBatch.delete(it.reference)
            }
            deletedSubjects.clear()

            editedSubjects.forEach {
                val update = hashMapOf<String, Any>()
                update.put("name", it.subject.name!!)
                update.put("color", it.subject.color!!)
                writeBatch.update(it.doc.reference, update)
            }
            editedSubjects.clear()

            val lastIndex = subjectList.value!!.size - 1
            subjectList.value!!.reverse()
            subjectList.value!!.apply {
                if (this.isEmpty()) {
                    return@runBatch
                }
                if (lastIndex == 0) {
                    writeBatch.update(this[0].doc.reference,
                        hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
                    writeBatch.update(this[0].doc.reference,
                        hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
                    return@runBatch
                }
            }
            subjectList.value!!.forEachIndexed { i, current ->
                when (i) {
                    0 -> {
                        current.apply {
                            if (this.doc["prevDocumentId"] as String? != null) {
                                writeBatch.update(this.doc.reference,
                                    hashMapOf("prevDocumentId" to null) as Map<String, Any?>)
                            }
                            val next = subjectList.value!![i + 1]
                            if (this.doc["nextDocumentId"] as String? != next.doc.id) {
                                writeBatch.update(this.doc.reference,
                                    hashMapOf("nextDocumentId" to next.doc.id) as Map<String, Any>)
                            }
                        }
                    }
                    lastIndex -> {
                        current.apply {
                            val prev = subjectList.value!![i - 1]
                            if (this.doc["prevDocumentId"] as String? != prev.doc.id) {
                                writeBatch.update(this.doc.reference,
                                    hashMapOf("prevDocumentId" to prev.doc.id) as Map<String, Any>)
                            }

                            if (this.doc["nextDocumentId"] as String? != null) {
                                writeBatch.update(this.doc.reference,
                                    hashMapOf("nextDocumentId" to null) as Map<String, Any?>)
                            }
                        }
                    }
                    else -> {
                        current.apply {
                            val prev = subjectList.value!![i - 1]
                            if (this.doc["prevDocumentId"] as String? != prev.doc.id) {
                                writeBatch.update(this.doc.reference,
                                    hashMapOf("prevDocumentId" to prev.doc.id) as Map<String, Any>)
                            }

                            val next = subjectList.value!![i + 1]
                            if (this.doc["nextDocumentId"] as String? != next.doc.id) {
                                writeBatch.update(this.doc.reference,
                                    hashMapOf("nextDocumentId" to next.doc.id) as Map<String, Any>)
                            }
                        }
                    }
                }
            }
        }
}
