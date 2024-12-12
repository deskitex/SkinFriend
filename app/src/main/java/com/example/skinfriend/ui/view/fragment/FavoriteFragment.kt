package com.example.skinfriend.ui.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skinfriend.R
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.databinding.FragmentFavoriteBinding
import com.example.skinfriend.databinding.FragmentHistoryBinding
import com.example.skinfriend.ui.model.FavoriteViewModel
import com.example.skinfriend.ui.model.ViewModelFactory
import com.example.skinfriend.ui.view.fragment.adapter.FavoriteAdapter
import com.example.skinfriend.ui.view.fragment.adapter.RecommendationAdapter
import com.example.skinfriend.util.setupRecyclerView

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteAdapter = FavoriteAdapter(object : FavoriteAdapter.OnButtonClickListener{
            override fun onButtonClicked(item: FavoriteEntity) {
                favoriteViewModel.deleteFavorite(item.productName)
            }
        })
        favoriteViewModel.getFavorite().observe(viewLifecycleOwner) {
            favoriteAdapter.submitList(it)
        }

        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView(
            binding.recycleView,
            favoriteAdapter,
            requireContext()
        )
    }
}