package kr.co.gooroomeelite.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_studystatus_bottomsheet.*
import kr.co.gooroomeelite.R


class StudystatusBottomsheetFragment : BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_studystatus_bottomsheet, container, false)
    }

    //바텀시트 세부 기능 구현
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //btn_button.setOnClickListener { }


        //btn_button.setOnClickListener { }


    }


}
