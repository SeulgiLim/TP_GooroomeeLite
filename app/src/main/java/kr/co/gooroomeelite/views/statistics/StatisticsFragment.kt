package kr.co.gooroomeelite.views.statistics

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ShareActionProvider
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.gun0912.tedpermission.PermissionListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.views.statistics.share.ShareActivity

class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pageView = inflater.inflate(R.layout.fragment_statistics, container, false)
        val tabs: TabLayout = pageView.findViewById(R.id.tabs)

        val dayFragment = DayFragment()
        val weekFragment = WeekFragment()
        val monthFragment = MonthFragment()

        parentFragmentManager.beginTransaction().add(R.id.chart_container, dayFragment).commit()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position

                var selected: Fragment? = null
                if (position == 0) {
                    selected = dayFragment
                } else if (position == 1) {
                    selected = weekFragment
                } else if (position == 2) {
                    selected = monthFragment
                }
                if (selected != null) {
                    parentFragmentManager.beginTransaction().replace(R.id.chart_container, selected)
                        .commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        
        val shareButton : Button = pageView.findViewById(R.id.share_button)
        shareButton.setOnClickListener{
            requestPermission()
        }

        return pageView
    }

    private fun requestPermission(): Boolean{
        var permissions = false
        Dexter.withContext(this.activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener,
                com.karumi.dexter.listener.single.PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    permissions = true      //p0=response(응답)
                    val shareIntent = Intent(context,ShareActivity::class.java)
                    startActivity(shareIntent)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    permissions = false
                    showError()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

                override fun onPermissionGranted() {
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                }

            })
            .check()
        return permissions
    }

    private fun showError(){
        Toast.makeText(
            this.requireContext(),
            "카메라/앨범의 권한을 허용해주세요",
            //"권한을 허용해야 앱을 사용할 수 있습니다"
            Toast.LENGTH_SHORT
        ).show()
    }
}

