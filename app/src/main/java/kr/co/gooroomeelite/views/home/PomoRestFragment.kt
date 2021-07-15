package kr.co.gooroomeelite.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.gooroomeelite.databinding.FragmentPomoRestBinding

// 뽀모도로 집중시간

class PomoRestFragment : Fragment() {

    private var _binding : FragmentPomoRestBinding? = null
    private val binding get () = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPomoRestBinding.inflate(inflater, container, false)
        return binding.root
    }
}