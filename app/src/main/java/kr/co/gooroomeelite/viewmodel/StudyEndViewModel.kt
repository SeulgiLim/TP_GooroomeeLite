package kr.co.gooroomeelite.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-12
 * @desc
 */
class StudyEndViewModel : ViewModel() {
    val db: FirebaseFirestore
    // 뮤터블 라이브 데이터 - 변경 가능한거
    // 라이브 데이터 - 읽기 전용


    companion object {
        const val TAG:String ="로그"
    }
    private val _today = MutableLiveData<String>()
    val today : LiveData<String>
    get() = _today

    //초기값 설정
    init {
        db = FirebaseFirestore.getInstance()
        Log.d(TAG,"StudyEndViewBodel - 생성자 호출")
        _today.value = ""
    }

    fun changePercent(){

    }



}