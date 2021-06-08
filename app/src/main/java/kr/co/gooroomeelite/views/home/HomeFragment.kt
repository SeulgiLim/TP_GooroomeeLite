package kr.co.gooroomeelite.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kr.co.gooroomeelite.databinding.FragmentHomeBinding
import kr.co.gooroomeelite.utils.LoginUtils.Companion.isLogin

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        binding.button.setOnClickListener {
            if(isLogin()) {
                FirebaseAuth.getInstance().signOut()
            }
        }

        return binding.root
    }
}