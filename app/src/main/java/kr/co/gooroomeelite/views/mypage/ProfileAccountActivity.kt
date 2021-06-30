package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityProfileAccountBinding
import kr.co.gooroomeelite.model.ContentDTO
import kr.co.gooroomeelite.utils.LoginUtils.Companion.getUid
import java.io.File

class ProfileAccountActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var firestore: FirebaseFirestore? = null
    var auth : FirebaseAuth?=null
    var email : String?= null
    private val isLoading = MutableLiveData<Boolean>()

    private var storageRef: StorageReference? = null
    private lateinit var binding: ActivityProfileAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAccountBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        email = auth?.currentUser?.email
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        getImage(getUid()!!)
        isLoading.value = false
        //Initiate storage
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

//        firestore?.collection("users")?.document(uid!!)?.get()?.addOnSuccessListener { ds ->
//            val nickname = ds.data?.get("nickname").toString()
//            val profileImageUrl: String = ds.data?.get("profileImageUrl").toString()
//            binding.edittext.setText(nickname)
//            Glide.with(this).load(profileImageUrl).into(binding.imageView2)
//        }


        firestore?.collection("users")?.document(getUid()!!)?.get()?.addOnSuccessListener { ds ->
            val contentDTO = ds.toObject(ContentDTO::class.java)
            val nickname = contentDTO!!.nickname
            val profileImageUrl = contentDTO!!.profileImageUrl
            binding.edittext.setText(nickname)
            if (profileImageUrl != null) {
                Glide.with(this).load(profileImageUrl).into(binding.imageView2)
            } else {
                binding.imageView2.setImageResource(R.drawable.ic_gooroomee_logo)
            }
        }

        binding.imageView2.setOnClickListener {
            //앨범 열기
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        //클릭시 업로드 메소드 수행
        binding.btnModifyOk.setOnClickListener {
            isLoading.value = true
            contentUploadandDelete()
        }
        with(supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(R.drawable.ic_back_icon)
            setTitle(R.string.profile_account)
        }


        isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

    }

//    init {
//        firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener {
//            if (it.isSuccessful) {
//                val contentDTOs = it.result?.toObject(ContentDTO::class.java)
//            }
//        }
//    }

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
//                    firestore?.collection("users")?.document(uid!!)?.set(contentDTO)
                    firestore?.collection("users")?.whereEqualTo("userId",email)?.get()
                        ?.addOnSuccessListener {
                            var data = hashMapOf<String,Any>()
                            data.put("profileImageUrl",uri.toString())
                            data.put("nickname",binding.edittext.text.toString())
                            firestore?.collection("users")!!.document(getUid()!!).update(data)
                        }

                    isLoading.value = false
                    finish()
                }
                setResult(Activity.RESULT_OK)
            }
        } else {storageRef!!.downloadUrl.addOnSuccessListener { uri ->
            val contentDTO = ContentDTO()
            contentDTO.nickname = binding.edittext.text.toString()
            firestore?.collection("users")?.whereEqualTo("userId",email)?.get()?.addOnSuccessListener {
                val data = hashMapOf<String, Any>()
                data["profileImageUrl"] = contentDTO.profileImageUrl!!
                data["nickname"] = contentDTO.nickname!!
                firestore?.collection("users")?.document(getUid()!!)?.update(data)
            }
            isLoading.value = false
            finish()
        }
            setResult(Activity.RESULT_OK)
        }
    }

    //갤러리에서 꺼낸 이미지를 세팅해주기.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == RESULT_OK) {
                //This is path to the selected image
                photoUri = data?.data
                binding.imageView2.setImageURI(photoUri)
            } else {
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }
    }

    //이미지를 세팅하기.
    private fun getImage(num:String) {
        val num = getUid()!!
        val file: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img")
        if (file?.isDirectory == null) {
            file?.mkdir()
        } else
            downloadImgNickname(num)
    }

    private fun downloadImgNickname(num:String) {
        val num = getUid()!!
        val filename = "profile$num.jpg"

        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference
        storageRef!!.child("profile_img/$filename")
            .child(filename).downloadUrl.addOnSuccessListener {
                storageRef!!.child("profile_img/$filename")
                    .child(filename).downloadUrl.addOnSuccessListener {
                        Glide.with(this).load(it).into(binding.imageView2)
                    }
                    .addOnSuccessListener {
                        Toast.makeText(this, "다운로드 되었습니다.", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "다운로드실패 되었습니다.", Toast.LENGTH_LONG).show()
                    }
            }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}




