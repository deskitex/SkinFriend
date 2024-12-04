package com.example.skinfriend.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skinfriend.R
import com.example.skinfriend.databinding.FragmentFavoriteBinding
import com.example.skinfriend.databinding.FragmentHistoryBinding
import com.example.skinfriend.ui.view.fragment.adapter.FavoriteAdapter
import com.example.skinfriend.ui.view.fragment.adapter.HistoryAdapter

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteAdapter: FavoriteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter()
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = favoriteAdapter
    }
}