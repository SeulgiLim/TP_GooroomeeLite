package kr.co.gooroomeelite.views.statistics.share.canvas

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.drawToBitmap
import kr.co.gooroomeelite.R

class CustomCanvas(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int=0 )
    : View(context, attrs, defStyleAttr), View.OnClickListener{

//    private val whitePathPaint = Paint().apply{
//        isAntiAlias = true// AntiAliasing은 그려지는 가장자리를 매끄럽게하지만 모양 내부에는 영향을주지 않는다.
//        color = Color.WHITE
//        style = Paint.Style.STROKE //이 스타일로 그린 도형과 텍스트는 페인트의 획 관련 필드에 따라 획이 그려진다.
//        strokeWidth = 8.0f
//    }
//
//    private val whiteTextPaint = Paint().apply{
//        textSize = 70.0f
//        textAlign = Paint.Align.LEFT
//        isAntiAlias = true
//        color = Color.WHITE
//        style = Paint.Style.FILL_AND_STROKE
//        strokeWidth = 3.0f
//    }

    //그리는 내용
    private lateinit var canvas : Canvas
    private var srcBitmap : Bitmap? = null

    private val logoMatrix = Matrix() //Matrix 클래스에는 좌표 변환을위한 3x3 행렬이 있다.

    private val logoIconWhite = BitmapFactory.decodeResource(this.resources, R.drawable.ic_group)

    fun initialized(uri: Uri){
        Log.d("aaaaCustomuri", uri.toString())
            //안드로이드 9의 신규 기능
            // 정확한 배율조정, 하드웨어 메모리에 대한 단계별 디코딩, 디코딩의 후처리 지원과 애니메이션 이미지 디코딩등을 포함하는 BitmapFactory보다 더 좋은 기능들을 지원한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            srcBitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, uri)
            ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            srcBitmap = BitmapDrawable(
                context.resources,
                MediaStore.Images.Media. getBitmap(context.contentResolver, uri)
            ).bitmap
        }
        invalidate()
    }

    private fun cropBitmap(){ //잘린 비트맵
        val srcBmp = srcBitmap!!
        val croppedBmp : Bitmap
        if(srcBmp.width >= srcBmp.height){
            croppedBmp = Bitmap.createBitmap(
                srcBmp,
                srcBmp.width/2 - srcBmp.height/2,
                0,
                srcBmp.height,
                srcBmp.height
                )
        }else{
            croppedBmp = Bitmap.createBitmap(
                srcBmp,
                0,
                srcBmp.height/2 - srcBmp.height/2,
                srcBmp.width,
                srcBmp.width
            )
        }
        this.srcBitmap = Bitmap.createScaledBitmap(croppedBmp,width,height,false)
    }
    //뷰가 그려지는 과정, 뷰의 크기를 정함
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeMax = if(widthMeasureSpec>heightMeasureSpec){
            heightMeasureSpec
        }else{
            widthMeasureSpec
        }//xml이 아닌 code상에서 조절
        layoutParams.height = sizeMax
        layoutParams.width = sizeMax
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas != null){
            this.canvas = canvas
            if(srcBitmap!=null){
                cropBitmap()
                setOnClickListener(this)
                initCanvas()
            }
        }
    }

    //canvas에 그리기
    private fun initCanvas(){
        if(::canvas.isInitialized){
            if(srcBitmap!=null){
                val croppedBitmap  = srcBitmap!!
                canvas.drawBitmap(croppedBitmap,0f,0f,null)
                //로고 그리기
                logoMatrix.setScale(0.15f,0.15f,(width-(logoIconWhite.width*0.15f)/2),height*0.05f)
                canvas.drawBitmap(logoIconWhite,logoMatrix,null)
            }
        }
    }

    fun saveCanvas() : Bitmap{
        return this.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

    override fun onClick(v: View?) {
        invalidate()
    }
}