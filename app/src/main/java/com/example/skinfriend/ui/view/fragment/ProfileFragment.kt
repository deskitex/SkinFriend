package com.example.skinfriend.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.skinfriend.R
import com.example.skinfriend.databinding.FragmentProfileBinding
import com.example.skinfriend.helper.SessionManager
import com.example.skinfriend.ui.view.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        binding.btnLogout.setOnClickListener {
            setupLogoutListener()
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(R.id.navigation_history)
        }

        binding.btnRekomendation.setOnClickListener {
            findNavController().navigate(R.id.navigation_favorite)
        }

        binding.tvUserName.text = "Hai ${sessionManager.getUserName()}"
        binding.tvNameProfile.text = "${sessionManager.getUserName()}"
        binding.tvEmail.text = "${sessionManager.getUserEmail()}"

    }

    private fun setupLogoutListener() {
        sessionManager.clearSession()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

