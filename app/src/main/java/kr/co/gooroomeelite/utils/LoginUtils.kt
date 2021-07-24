package kr.co.gooroomeelite.utils

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class LoginUtils {
    companion object {
        fun currentUser() = FirebaseAuth.getInstance().currentUser
        fun isLogin() = currentUser() != null
        fun getUid() = currentUser()?.uid
        fun signOut(context: Context) {
            FirebaseAuth.getInstance().signOut()
            val editor = context.getSharedPreferences("studyTime",Context.MODE_PRIVATE).edit()
            editor.clear()
            editor.apply()
        }
        fun deleteAccount(context: Context) {
            FirebaseAuth.getInstance().currentUser?.delete()
            val editor = context.getSharedPreferences("studyTime",Context.MODE_PRIVATE).edit()
            editor.clear()
            editor.apply()
        }
    }
}