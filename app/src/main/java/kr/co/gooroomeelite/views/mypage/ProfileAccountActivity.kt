package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityProfileAccountBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileAccountActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM =0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null

    var storageRef : StorageReference? = null
    private lateinit var binding:ActivityProfileAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        getImage(10)

        //Initiate storage
        storage = FirebaseStorage.getInstance()

        binding.imageView2.setOnClickListener {
            //앨범 열기
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        }
        //클릭시 업로드 메소드 수행
        binding.btnModifyOk.setOnClickListener {
            contentUploadandDelete()
        }
        with(supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(R.drawable.ic_back_icon)
            setTitle(R.string.profile_account)
        }

    }
    private fun contentUpload() {
        //중복 되지 않게 파일 이름
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        //파이어베이스에 업로드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,"업로드 되었습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun contentUploadandDelete(){
        var num : Int = 10
        var filename = "profile$num.jpg"

        var storageRef = storage?.reference?.child("profile_img/$filename")?.child(filename)

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,"업로드 되었습니다.", Toast.LENGTH_LONG).show()
        }
        storageRef?.putFile(photoUri!!)?.addOnFailureListener {
            Toast.makeText(this,"실패 되었습니다.", Toast.LENGTH_LONG).show()
        }
        var desertRef = storage?.reference?.child("profile_img/$filename")?.child(filename)

        desertRef?.delete()?.addOnSuccessListener {
            Toast.makeText(this,"삭제 되었습니다.", Toast.LENGTH_LONG).show()
        }
        desertRef?.delete()?.addOnFailureListener {
            Toast.makeText(this,"삭제실패 되었습니다.", Toast.LENGTH_LONG).show()
        }
    }

    //갤러리에서 꺼낸 이미지를 세팅해주기.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if (resultCode == RESULT_OK){
                //This is path to the selected image
                photoUri = data?.data
                binding.imageView2.setImageURI(photoUri)

            }else{
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }
    }

    //이미지를 세팅하기.
    private fun getImage(num:Int){
        var file : File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img")
        if(file?.isDirectory == null){
            file?.mkdir()
        }
        else
            downloadImg(num)
    }
    private fun downloadImg(num: Int){
        var filename = "profile$num.jpg"
        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference
        storageRef!!.child("profile_img/$filename").child(filename).downloadUrl.addOnSuccessListener{
            Glide.with(this).load(it).into(binding.imageView2)
        }
            .addOnSuccessListener {

                Toast.makeText(this,"다운로드 되었습니다.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {

                Toast.makeText(this,"다운로드실패 되었습니다.", Toast.LENGTH_LONG).show()
            }
    }

}