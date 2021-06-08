package kr.co.gooroomeelite.utils

import com.google.firebase.auth.FirebaseAuth

class LoginUtils {
    companion object {
        fun currentUser() = FirebaseAuth.getInstance().currentUser
        fun isLogin() = currentUser() != null
    }
}