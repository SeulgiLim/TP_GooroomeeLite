package kr.co.gooroomeelite.views.statistics.share

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import kotlinx.android.synthetic.main.fragment_statistics.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityStickerBinding
import kr.co.gooroomeelite.entity.Subject
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import kr.co.gooroomeelite.views.common.MainActivity
import kr.co.gooroomeelite.views.login.LoginActivity
import kr.co.gooroomeelite.views.statistics.StatisticsFragment
import kr.co.gooroomeelite.views.statistics.share.extensions.loadCenterCrop
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class StickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStickerBinding

    private var root: View? = null
    private lateinit var statisticsFragment: StatisticsFragment

    private val shareButtonViewImage: Boolean = false


    // 현재 날짜/시간 가져오기
    val dateNow: LocalDateTime = LocalDateTime.now()
    val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val textformatterString: String = dateNow.format(textformatter)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStickerBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(binding.root)
//        initToolBar()
        val pictures = intent.getStringExtra("picture")
        if (pictures != null) {
            imageContent(pictures)
        }
        //갤러리 이미지
        val gallery = intent.getStringExtra("gallery")
        if (gallery != null) {
            binding.imageCapture.loadCenterCrop(url = gallery)
        }

        //취소하기
//        binding.exitButtons.setOnClickListener{
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
//        }

        binding.shareCancel.setOnClickListener{finish()}

        //통계 프래그먼트 페이지로 이동
        binding.close.setOnClickListener {
            finish()
//            startActivity(Intent(this, MainActivity::class.java))
//            statisticsFragment = StatisticsFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer,statisticsFragment).commit()
//            replaceFragment(this.startActivityFromFragment())
        }
        //공유하기
        binding.shareButtons.setOnClickListener{ takeAndShareScreenShot(pictures.toString()) }



        //현재시간
//        binding.nowTime.setText(textformatterString)
        getTotalStudy()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }

    fun getTotalStudy() {
        FirebaseFirestore.getInstance()
            .collection("subject")
            .whereEqualTo("uid", getUid())
            .get()
            .addOnSuccessListener {
                val subject = it.toObjects(Subject::class.java)
                var studytimetodaylist = mutableListOf<Int>()
                for (i in 0..subject.size - 1) {
                    studytimetodaylist.add(subject[i].studytime)
                }
                val todayStudySum : Int = studytimetodaylist.sum()
                Log.d("sum",todayStudySum.toString())
                binding.hourStudytime.text = (todayStudySum/60).toString() + "h"
                binding.minuteStudytime.text = (todayStudySum%60).toString() + "m"
//                ${studytime / 60}시간 ${studytime % 60}분"
//                todayStudyTime.value = studytimetodaylist.sum()
//                FirebaseFirestore.getInstance().collection("users").document(getUid()!!).update("todaystudytime",todayStudyTime.value)
            }
    }



//    private fun initToolBar() {
//        val toolbar = binding.shareToolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }
    //뒤로가기 눌렀을 때 작동
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun imageContent(pictures: String) {
        Handler(Looper.getMainLooper()).post {
            binding.imageCapture.loadCenterCrop(url = pictures)
        }
    }

    private fun takeAndShareScreenShot(uri: String) {
        val buttonView: Button = findViewById(R.id.share_buttons)
        Instacapture.capture(this, object : SimpleScreenCapturingListener() {
            override fun onCaptureComplete(captureview: Bitmap) {
                val capture: FrameLayout = findViewById<FrameLayout>(R.id.previewStickerImageView)
                capture.buildDrawingCache()
                val captureview: Bitmap = capture.getDrawingCache()
                val uri = saveImageExternal(captureview)
                shareImageURI(uri)
            }
        }, buttonView)
    }


    private fun saveImageExternal(image: Bitmap): Uri? {
        val filename = "gooroomeelite_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var uri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, getString(R.string.app_content_path))
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val contentResolver = this.contentResolver

        contentResolver.also { resolver ->
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = uri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { image.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(uri!!, contentValues, null, null)

        return uri!!
    }


    private fun shareImageURI(uri: Uri?): Boolean {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, "Send to"))
        return shareButtonViewImage
    }
    
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        startActivity(Intent(this, ShareActivity::class.java))
        finish()
    }
}