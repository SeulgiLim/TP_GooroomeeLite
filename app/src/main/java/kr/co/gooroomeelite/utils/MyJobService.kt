package kr.co.gooroomeelite.utils

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid

class MyJobService : JobService() {
    var firestore: FirebaseFirestore? = null

    companion object {
        private val TAG= "MyJobService"
    }


    override fun onStartJob(params: JobParameters?): Boolean {

        firestore = FirebaseFirestore.getInstance()
        Log.d("asdfasdf", "1234")
        Log.d("asdfasdf","${getUid()}")
        Log.d("asdfasdf","${getUid()}")

        firestore?.collection("subject")?.whereEqualTo("uid",getUid()!!)?.get()?.addOnSuccessListener {
            firestore!!.collection("subject").document(getUid()!!).update(hashMapOf("studyTime" to 0) as Map<String, Any>) // 업데이트 부분
                .addOnSuccessListener {
                    Log.d("asdfasdf", "123")
                }.addOnFailureListener {
                    Log.d("asdfasdf","345")
                }
        }
    return false
    }


    override fun onStopJob(params: JobParameters?): Boolean {
        Log.e("TEST","Stop")
        return false
    }
}