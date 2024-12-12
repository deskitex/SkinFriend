package com.example.skinfriend.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.skinfriend.R
import com.example.skinfriend.databinding.FragmentHistoryBinding
import com.example.skinfriend.ui.model.HistoryViewModel
import com.example.skinfriend.ui.model.ViewModelFactory
import com.example.skinfriend.ui.view.fragment.adapter.HistoryAdapter
import com.example.skinfriend.ui.view.fragment.adapter.RecommendationAdapter
import com.example.skinfriend.util.setupRecyclerView


class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historyAdapter = HistoryAdapter()

        historyViewModel.getHistory().observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
        }

        setupRecyclerView(
            binding.recyclerView,
            historyAdapter,
            requireActivity()
        )

    }
}