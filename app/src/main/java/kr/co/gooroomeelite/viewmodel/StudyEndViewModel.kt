package kr.co.gooroomeelite.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-12
 * @desc
 */
class StudyEndViewModel : ViewModel() {
    var firestore : FirebaseFirestore? = null
    val db: FirebaseFirestore

    private val _today = MutableLiveData<String>()
    private val myStudyGoal = MutableLiveData<Int>()
    private val todayStudyTime = MutableLiveData<Int>()

    //초기값 설정
    init {
        db = FirebaseFirestore.getInstance()
        Log.d(TAG,"StudyEndViewBodel - 생성자 호출")
        _today.value = ""
        myStudyGoal.value = 0
        todayStudyTime.value = 0
        setting()
    }

    fun setting(){
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(getUid()!!).get().addOnSuccessListener {
            Log.e("asdfasdf","$it")
            Log.e("asdfasdf","${it.toObject(ContentDTO::class.java)}")
            Log.e("asdfasdf","${it.toObject(ContentDTO::class.java)?.todaystudytime}")
        }
    }
}