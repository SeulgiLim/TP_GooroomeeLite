package kr.co.gooroomeelite.views.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.gooroomeelite.R

class DayFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dayView = inflater.inflate(R.layout.fragment_day,container,false)
        return dayView
    }
}