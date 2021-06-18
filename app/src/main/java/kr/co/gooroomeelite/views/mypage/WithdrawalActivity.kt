package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-06-09
 * @desc
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.databinding.ActivityWithdrawalBinding

class WithdrawalActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWithdrawalBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        with(supportActionBar) {
            this!!.setDisplayHomeAsUpEnabled(true)
            this.setHomeAsUpIndicator(R.drawable.ic_back_icon)
            setTitle(R.string.withdrawal)
        }
        binding.checkBox.setOnCheckedChangeListener { _, _ -> checkall() }
        binding.checkBox2.setOnCheckedChangeListener { _, _ -> checkall() }
        binding.checkBox3.setOnCheckedChangeListener { _, _ -> checkall() }

                    // 회원탈퇴 //
        binding.checkbefore.setOnClickListener {
            val mWithdrawalView =
                LayoutInflater.from(this).inflate(R.layout.fragment_dialog_withdrawal, null)
            val mBuilder =
                androidx.appcompat.app.AlertDialog.Builder(this).setView(mWithdrawalView)
            val mAlertDialog = mBuilder.show().apply {
                window?.setBackgroundDrawable(null)
            }
            val okButton = mWithdrawalView.findViewById<Button>(R.id.btn_withdrawl_ok)
            val cancelButton = mWithdrawalView.findViewById<Button>(R.id.btn_withdrawl_no)
            okButton.setOnClickListener {
                //탈퇴

                mAuth.currentUser!!.delete()

                //파이어베이스 회원탈퇴//



                Toast.makeText(this,"탈퇴되었습니다.",Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
//                finish()
            }
            cancelButton.setOnClickListener {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
        }
    }
    private fun checkall (){
        if (binding.checkBox.isChecked and binding.checkBox2.isChecked and binding.checkBox3.isChecked ){
            binding.checkbefore.setBackgroundColor(resources.getColor(R.color.skyBlue))
            binding.checkbefore.setTextColor(resources.getColor(R.color.white))
            binding.checkbefore.isClickable=true
        }
        else{
            binding.checkbefore.setBackgroundColor(resources.getColor(R.color.divide2))
            binding.checkbefore.setTextColor(resources.getColor(R.color.black))
            binding.checkbefore.isClickable=false
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}