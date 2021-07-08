package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-08
 * @desc
 */
data class PrivacyItem (
    val title : String ,
    val content : String

)

data class ServiceItem (
    val title : String ,
    val content : String

)

data class OpenSourceItem (
    val title : String ,
    val content : String

)

data class MusicItem(val tvmusic: String,
                     val music: Int
)