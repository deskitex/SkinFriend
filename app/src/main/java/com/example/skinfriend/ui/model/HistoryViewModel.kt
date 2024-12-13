package com.example.skinfriend.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.entity.HistoryWithRecommendations
import com.example.skinfriend.data.local.entity.RecommendationEntity
import com.example.skinfriend.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    fun getHistoryWithRecommendationsLive(historyId: Int): LiveData<HistoryWithRecommendations> {
        return repository.getHistoryWithRecommendationsLive(historyId)
    }

    suspend fun getHistoryItem(id: Int): HistoryWithRecommendations = repository.getHistoryItem(id)

    fun getHistoryTake3(): LiveData<List<HistoryEntity>> = getHistory().map { list ->
        list.takeLast(3)
    }

    fun getHistory(): LiveData<List<HistoryEntity>> = repository.getHistory()

    suspend fun insertHistory(history: HistoryEntity): Long {
        return repository.insertHistory(history)
    }

    fun insertRecommendation(recommendation: List<RecommendationEntity>) =
        viewModelScope.launch {
            repository.insertRecommendation(recommendation)
        }
}
