package kr.co.gooroomeelite.views.mypage
/**
 * @author Gnoss
 * @email silmxmail@naver.com
 * @created 2021-07-15
 * @desc
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kr.co.gooroomeelite.R
import kr.co.gooroomeelite.adapter.MusicAdapter
import kr.co.gooroomeelite.databinding.FragmentBottomSheetBinding

class BottomSheetFragment(private val owner : AppCompatActivity) : BottomSheetDialogFragment() {
    lateinit var binding : FragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater,container,false)
        with(binding){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                adapter =MusicAdapter(owner,musicData())
            }
        }
        return binding.root
    }

    private fun musicData(): MutableList<MusicItem> {
        val musicList = mutableListOf<MusicItem>()
        return musicList.apply {
            add(MusicItem("모닥불소리",R.raw.firewood))
            add(MusicItem("카페",R.raw.cafe))
            add(MusicItem("도서관",R.raw.library))
            add(MusicItem("파도 소리",R.raw.wave))
            add(MusicItem("빗소리",R.raw.raindrop))
        }
    }


}