package kr.co.gooroomeelite.views.statistics

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.item_marker_view.view.*

//막대 차트 값
@SuppressLint("ViewConstructor")
class CustomMarketView(context: Context, layoutResources: Int) :
    MarkerView(context, layoutResources) {

    //클릭 시 새로 고쳐야하는 그래픽 데이
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tv_chart_marker.text = e?.y.toString()
        super.refreshContent(e, highlight)
    }
    //경계 초과시 xy 이동 방법, xy 오프셋 값 계산
    override fun getOffset(): MPPointF? {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
