package kr.co.gooroomeelite.utils

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.R

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
//        fun loginBuilder() = AuthUI.getInstance().createSignInIntentBuilder()
//            .setTheme(R.style.Theme_Design_Light_NoActionBar)
//            .setLogo(AuthUI.NO_LOGO)//아이콘 로고 등록하기
//            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build(), AuthUI.IdpConfig.EmailBuilder().build()))
////                .setTosAndPrivacyPolicyUrls("https://naver.com", "https://google.com")
//            .setIsSmartLockEnabled(false)// default = true
//            .build()
    }
}