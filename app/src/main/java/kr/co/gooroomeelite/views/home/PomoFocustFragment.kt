package kr.co.gooroomeelite.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import kr.co.gooroomeelite.databinding.FragmentPomoFocustBinding

class PomoFocustFragment : Fragment() {

    private var _binding : FragmentPomoFocustBinding? = null
    private val binding get () = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPomoFocustBinding.inflate(inflater, container, false)
        return binding.root
    }
}

