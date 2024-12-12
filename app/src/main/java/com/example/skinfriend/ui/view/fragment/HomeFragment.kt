package com.example.skinfriend.ui.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.skinfriend.R
import com.example.skinfriend.preferences.UserPreferences
import com.example.skinfriend.databinding.FragmentHomeBinding
import com.example.skinfriend.ui.model.HistoryViewModel
import com.example.skinfriend.ui.model.HomeViewModel
import com.example.skinfriend.ui.model.NewsViewModel
import com.example.skinfriend.ui.model.ViewModelFactory
import com.example.skinfriend.ui.view.CameraActivity
import com.example.skinfriend.ui.view.QuizActivity
import com.example.skinfriend.ui.view.fragment.adapter.HistoryAdapter
import com.example.skinfriend.ui.view.fragment.adapter.NewsAdapter
import com.example.skinfriend.util.setupRecyclerView
import com.example.skinfriend.util.showToast


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreferences: UserPreferences
    private var dayStreak: Int = 0


    private val historyViewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val newsViewModel: NewsViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(requireActivity(), "Permission request granted")

            } else {
                showToast(requireActivity(), "Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViewHistory()
        setupRecyclerViewNews()

        homeViewModel.checkAndUpdateUserStreak()
        setupObservers()


        binding.daily1.setOnClickListener{
            val intent = Intent(requireActivity(), QuizActivity::class.java)
            startActivity(intent)
        }

        binding.daily2.setOnClickListener{
            startCameraX()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences.getInstance(requireContext())

    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadDayStreak() // Memuat ulang streak
        homeViewModel.loadStressResult() // Memuat ulang hasil kuis
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers(){
        homeViewModel.dayStreak.observe(viewLifecycleOwner) { streak ->
            binding.dayStreak.text = streak.toString()
        }

        homeViewModel.stressResult.observe(viewLifecycleOwner) { result ->
            binding.stressLevel.text = result
        }
    }

    private fun startCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else{
            startActivity(intent)
        }
    }

    private fun setupRecyclerViewHistory(){
        val historyAdapter = HistoryAdapter()

        historyViewModel.getHistoryTake3().observe(requireActivity()) { history ->
            historyAdapter.submitList(history)

            setupRecyclerView(
                binding.rvHistory,
                historyAdapter,
                requireActivity()
            )
        }
    }

    private fun setupRecyclerViewNews(){
        val newsAdapter = NewsAdapter()

        newsViewModel.listNews.observe(requireActivity()) { news ->
            newsAdapter.submitList(news)
        }
        setupRecyclerView(
            binding.rvNews,
            newsAdapter,
            requireActivity()
        )
        newsViewModel.showListNews()
    }
}