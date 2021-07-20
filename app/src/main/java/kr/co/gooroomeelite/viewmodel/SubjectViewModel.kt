package kr.co.gooroomeelite.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.entity.ReadSubejct
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.entity.Subjects
import kr.co.gooroomeelite.utils.LoginUtils
import kr.co.gooroomeelite.utils.LoginUtils.Companion.currentUser
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import java.util.LinkedList

//데이터를 activity가 아니라 viewModel이 관리하게 할 것
// 데이터의 변경사항을 알려주는 라이브 데이터를 가지는 뷰모델
class SubjectViewModel : ViewModel() {
    val db: FirebaseFirestore
    val subjectList = MutableLiveData<LinkedList<DocumentSnapshot>>()
    val uid: String
    //통계 페이지에서 사용
    val list = MutableLiveData<MutableList<Subject>>()

    private var subjectListValue: MutableList<Subject> = mutableListOf()
    var subject: Subject? = null

    init {
        db = FirebaseFirestore.getInstance()
        subjectList.value = LinkedList()
        uid = currentUser()!!.uid
        listSubject()
        fetchSubjectList()
    }

    fun listSubject(){
        FirebaseFirestore.getInstance()
            .collection("subject")
            .whereEqualTo("uid", LoginUtils.getUid()!!)
            .get() //값이 변경 시 바로 값이 변경된다.
            .addOnSuccessListener { docs ->
                if(docs != null) {
//                    val tmp = mutableListOf<Subject>()
                    docs.documents.forEach {
                        subject = it.toObject(Subject::class.java)!!
                        subjectListValue.add(subject!!)
                    }
                    list.value = subjectListValue
                }
            }
    }

    //과목별 전체
    private fun fetchSubjectList() { //주제 목록 가져오기
        db.collection("subject")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    if(value.documents.isEmpty()) {//문서
                        subjectList.value = LinkedList()
                        return@addSnapshotListener
                    }
                    val tmp = hashMapOf<String, DocumentSnapshot>()
                    var current: DocumentSnapshot? = null
                    value.documents.forEach {
                        Log.d("mmm",value.toString())
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
                    tmp2.reverse()
                    subjectList.value = tmp2
                }
                // 최근 순 정렬
                // subjectList.value = value.documents.sortedBy { it.getDate("timestamp") }
            }
    }


    fun addSubject(item: Subject) {
        var prevSubject: DocumentSnapshot? = null
        if (subjectList.value!!.size > 0) {
            prevSubject = subjectList.value!!.first
        }
        // 측정시간 서버로 바로 보내는 방법
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
