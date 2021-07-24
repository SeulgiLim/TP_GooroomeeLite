package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-15
 * @desc
 */
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityProfileUpdateBinding
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileUpdateActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var REQUEST_IMAGE_CAPTURE = 1
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var email: String? = null
    var imm: InputMethodManager? = null
    private val maxlength = 6
    private val isLoading = MutableLiveData<Boolean>()
    lateinit var currentPhotoPath : String

    private var storageRef: StorageReference? = null
    private lateinit var binding: ActivityProfileUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        email = auth?.currentUser?.email
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        setContentView(binding.root)

        getImage(getUid()!!)
        isLoading.value = false

        //Initiate storage
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        firestore?.collection("users")?.document(getUid()!!)?.get()?.addOnSuccessListener { ds ->
            val contentDTO = ds.toObject(ContentDTO::class.java)
            val nickname = contentDTO!!.nickname
            val profileImageUrl = contentDTO!!.profileImageUrl
            binding.edittext.setText(nickname)
            if (profileImageUrl != null) {
                Glide.with(this).load(profileImageUrl).into(binding.imageView2)

                binding.imageView2.setColorFilter(R.color.deam)
            } else {
                binding.imageView2.setImageResource(R.drawable.ic_gooroomee_logo)
                binding.imageView2.setColorFilter(R.color.orange, PorterDuff.Mode.SRC_ATOP);

            }
        }
        // 카메라 권한 요청
        fun requestPermission() {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
                REQUEST_IMAGE_CAPTURE)
        }

        // 카메라 권한 체크
        fun checkPersmission(): Boolean {
            return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }

        // 권한요청 결과
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "카메라 허가")
            }else{
                Log.d("TAG","카메라 허가 필요")
            }
        }

        //버튼 클릭시 닉네임 제거
        binding.clearText.setOnClickListener {
            binding.edittext.text.clear()
        }

        binding.edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.edittext.apply {
                    if (this.isFocusable && s.toString() != "") {
                        val string: String = s.toString()
                        val len = string.length
                        if (len > maxlength) {
                            this.setText(string.substring(0, maxlength))
                            this.setSelection(maxlength)
                        } else {
                            binding.textCount.text = "$len / $maxlength"
                        }
                    } else {
                        binding.textCount.text = "0 / $maxlength"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.imageView2.setOnClickListener {

            val mAlbumView =
                LayoutInflater.from(this).inflate(R.layout.fragment_album, null)
            val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(mAlbumView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
                window?.setGravity(Gravity.BOTTOM)
            }
            val albumButton = mAlbumView.findViewById<TextView>(R.id.btn_album)
            val defaultButton = mAlbumView.findViewById<TextView>(R.id.btn_default)
            val photoButton = mAlbumView.findViewById<TextView>(R.id.btn_takephoto)

            photoButton.setOnClickListener{
                if (checkPersmission()){
                    dispatchTakePictureIntent()
                }
                else{
                    requestPermission()
                }
            }
            albumButton.setOnClickListener {
                //앨범 선택
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
            }
            defaultButton.setOnClickListener {
                //기본값
                binding.imageView2.setImageResource(R.drawable.ic_gooroomee_logo)
                binding.imageView2.setColorFilter(R.color.deam)
                contentUploadDefault()
            }
        }


        //클릭시 업로드 메소드 수행
        binding.btnModifyOk.setOnClickListener {
            isLoading.value = true
            contentUploadandDelete()
        }

        isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun contentUploadandDelete() {
        val num: String = getUid()!!
        val filename = "profile$num.jpg"
        val storageRef = storage?.reference?.child("profile_img/$filename")?.child(filename)

        if (photoUri != null) {
            storageRef!!.putFile(photoUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val contentDTO = ContentDTO()
                    //이미지 주소
                    contentDTO.profileImageUrl = uri.toString()
                    //닉네임
                    contentDTO.nickname = binding.edittext.text.toString()
                    firestore?.collection("users")?.whereEqualTo("userId", email)?.get()
                        ?.addOnSuccessListener {
                            val data = hashMapOf<String, Any>()
                            data["profileImageUrl"] = contentDTO.profileImageUrl!!
                            data["nickname"] = contentDTO.nickname!!
                            firestore?.collection("users")!!.document(getUid()!!).update(data)
                        }

                    isLoading.value = false
                    finish()
                }
                setResult(Activity.RESULT_OK)
            }
        } else {
            storageRef!!.downloadUrl.addOnSuccessListener { uri ->
                val contentDTO = ContentDTO()
                contentDTO.nickname = binding.edittext.text.toString()
                firestore?.collection("users")?.whereEqualTo("userId", email)?.get()
                    ?.addOnSuccessListener {
                        val data = hashMapOf<String, Any>()
                        data["nickname"] = contentDTO.nickname!!

                        firestore?.collection("users")?.document(getUid()!!)?.update(data)
                    }
                isLoading.value = false
                finish()
            }
            setResult(Activity.RESULT_OK)
        }
    }
    private fun contentUploadDefault() {
        val num: String = getUid()!!
        val filename = "profile$num.jpg"
        val storageRef = storage?.reference?.child("profile_img/$filename")?.child(filename)

            storageRef!!.downloadUrl.addOnSuccessListener {
                storageRef.delete()
                val contentDTO = ContentDTO()
                contentDTO.profileImageUrl = null
                firestore?.collection("users")?.whereEqualTo("userId", email)?.get()
                    ?.addOnSuccessListener {
                        val data = hashMapOf<String, Any?>()
                        data["profileImageUrl"] = contentDTO.profileImageUrl

                        firestore?.collection("users")?.document(getUid()!!)?.update(data)
                    }
                isLoading.value = false
                finish()
            }
            setResult(Activity.RESULT_OK)
        }


    //갤러리에서 꺼낸 이미지를 세팅해주기.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            0 -> {
                if (resultCode == RESULT_OK) {
                    photoUri = data?.data
                    binding.imageView2.setImageURI(photoUri)
                } else {
                    finish()
                }
            }
            1 -> {
                if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
                    // 카메라로부터 받은 데이터가 있을경우에만
                    val file = File(currentPhotoPath)
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media
                            .getBitmap(contentResolver, Uri.fromFile(file))
                        photoUri = getImageUri(this,bitmap)
                        binding.imageView2.setImageURI(photoUri)
                    }
                    else{
                        val decode = ImageDecoder.createSource(this.contentResolver,
                            Uri.fromFile(file))
                        val bitmap = ImageDecoder.decodeBitmap(decode)
                        photoUri = getImageUri(this,bitmap)
                        binding.imageView2.setImageURI(photoUri)
                    }
                }
            }
        }
    }
    //이미지를 세팅하기.
    private fun getImage(num: String) {
        val num = getUid()!!
        val file: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img")
        if (file?.isDirectory == null) {
            file?.mkdir()
        } else
            downloadImgNickname(num)
    }

    private fun downloadImgNickname(num: String) {
        val num = getUid()!!
        val filename = "profile$num.jpg"

        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference
        storageRef!!.child("profile_img/$filename")
            .child(filename).downloadUrl.addOnSuccessListener {
                storageRef!!.child("profile_img/$filename")
                    .child(filename).downloadUrl.addOnSuccessListener {
                        Glide.with(this).load(it).into(binding.imageView2)
//                        binding.imageView2.setColorFilter(R.color.deam)
                    }
            }
    }

    fun hideKeyboard(v: View) {
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                // 찍은 사진을 그림파일로 만들기
                val photoFile: File? =
                    try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("TAG", "그림파일 만드는도중 에러생김")
                        null
                    }

                // 그림파일을 성공적으로 만들었다면 onActivityForResult로 보내기
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this, "kr.co.gooroomeelite.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    // 카메라로 촬영한 이미지를 파일로 저장해준다
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

}
