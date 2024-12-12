package com.example.skinfriend.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.repository.FavoriteRepository
import com.example.skinfriend.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {
    fun getFavorite(): LiveData<List<FavoriteEntity>> = repository.getFavorite()

    fun insertFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.insertFavorite(favorite)
        }
    }

    fun deleteFavorite(productName: String) {
        viewModelScope.launch {
            repository.deleteFavorite(productName)
        }
    }

    fun isFavoriteSync(productName: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isFavorite = repository.isEventFavoriteSync(productName)
            callback(isFavorite)
        }
    }
}