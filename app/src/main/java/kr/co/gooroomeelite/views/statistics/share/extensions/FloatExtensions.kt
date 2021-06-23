package kr.co.gooroomeelite.views.statistics.share.extensions

import android.content.res.Resources
//dp가 pixel로 변형할 수 있도록 다음과 같이 바꿔준다.
internal fun Float.fromDpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}
