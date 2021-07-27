package kr.co.gooroomeelite.entity

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Weekly(
    var monDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY),//해당 주차의 월
    var tuesDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.TUESDAY),//해당 주차의 화
    var wednesDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.WEDNESDAY),//해당 주차의 수
    var thursDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.THURSDAY),//해당 주차의 목
    var friDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.FRIDAY),//해당 주차의 금
    var saturDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.SATURDAY),//해당 주차의 토
    var sunDay: LocalDateTime = LocalDateTime.now().with(DayOfWeek.SUNDAY)//해당 주차의 일
)

