package kr.co.gooroomeelite.entity

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class EditSubject(
    val subject : Subject,
    val doc : DocumentSnapshot
)
