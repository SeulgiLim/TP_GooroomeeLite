package kr.co.gooroomeelite.entity

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Subject(
    val uid: String? = null,
    val name: String? = null,
    val color: String? = null,
    val studytime: Int = 0,
    var prevDocumentId: String? = null,
    var nextDocumentId: String? = null,
    //

    @ServerTimestamp
    val timestamp: Date? = null
) : Serializable {
    constructor(uid:String?, name:String, color:String) : this(uid, name, color, 0, null, null, Date())
}
