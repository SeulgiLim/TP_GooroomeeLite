package kr.co.gooroomeelite.views.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.gooroomeelite.R


class MonthFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dayView = inflater.inflate(R.layout.fragment_month, container, false)
        return dayView
    }
}