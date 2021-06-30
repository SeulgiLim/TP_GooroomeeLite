package kr.co.gooroomeelite.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SmallRoundedSquare : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val paint = Paint()

    init {
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRoundRect(0f, 0f, dpToPx(16), dpToPx(16), dpToPx(3), dpToPx(3), paint)
    }

    fun dpToPx(dp: Int): Float {
        return dp * resources.displayMetrics.density + 0.5f
    }
}