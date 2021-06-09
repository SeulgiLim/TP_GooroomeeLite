package kr.co.gooroomeelite.views.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.gooroomeelite.R

class WeekFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val weekView = inflater.inflate(R.layout.fragment_week, container, false)
        return weekView
    }

}