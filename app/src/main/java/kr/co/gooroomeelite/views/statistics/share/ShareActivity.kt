package kr.co.gooroomeelite.views.statistics.share

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import kr.co.gooroomeelite.R

class ShareActivity : AppCompatActivity() {
    private lateinit var mSurfaceViewHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
    }
}