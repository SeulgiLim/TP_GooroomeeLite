package kr.co.gooroomeelite.views.statistics.share.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

//이미지뷰를 가져올 수 있는 함수를 미리 세팅!!
private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

internal fun ImageView.clear() = Glide.with(context).clear(this)

internal fun ImageView.loadCenterCrop(url: String, corner : Float = 0f) {
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (corner > 0) transforms(CenterCrop(), RoundedCorners(corner.fromDpToPx()))
        }
        .into(this)
}

internal fun ImageView.loadCenterCropp(url: String, corner : Float = 0f) {
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (corner > 0) transforms(CenterCrop(), RoundedCorners(corner.fromDpToPx()))
        }
        .into(this)
}


//.apply {
//    if (corner > 0) transforms(CenterCrop(), RoundedCorners(corner.fromDpToPx()))
//}
//internal fun ImageView.loadCenterCrop(url: String) {
//    Glide.with(this)
//        .load(url)
//        .transition(DrawableTransitionOptions.withCrossFade(factory))
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .into(this)
//}
