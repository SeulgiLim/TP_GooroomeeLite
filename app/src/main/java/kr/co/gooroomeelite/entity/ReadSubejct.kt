package kr.co.gooroomeelite.entity

import com.google.firebase.firestore.DocumentSnapshot

data class ReadSubejct(
    val subject : Subjects,
    val doc : DocumentSnapshot
)
