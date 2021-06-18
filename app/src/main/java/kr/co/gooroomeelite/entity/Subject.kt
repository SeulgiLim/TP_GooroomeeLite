package kr.co.gooroomeelite.entity

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Subject(
    val uid: String?,
    val name: String,
    val color: String,
    val studytime: Int,
    @ServerTimestamp
    val timestamp: Date?
) : Serializable {
    constructor(uid:String?, name:String, color:String) : this(uid, name, color, 0, Date())
}
