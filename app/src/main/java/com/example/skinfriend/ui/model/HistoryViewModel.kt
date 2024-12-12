package com.example.skinfriend.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {
    fun getHistory(): LiveData<List<HistoryEntity>> = repository.getHistory()

    fun getHistoryTake3(): LiveData<List<HistoryEntity>> = getHistory().map { list ->
        list.takeLast(3)
    }

    fun insertHistory(history: List<HistoryEntity>) =
        viewModelScope.launch {
            repository.insertHistory(history)
        }
}