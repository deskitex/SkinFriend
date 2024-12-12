package com.example.skinfriend.data.repository

import androidx.lifecycle.LiveData
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.room.HistoryDao

class HistoryRepository private constructor(
    private val historyDao: HistoryDao,
) {
    fun getHistory(): LiveData<List<HistoryEntity>> = historyDao.getHistory()

    suspend fun insertHistory(history: List<HistoryEntity>) {
        historyDao.insertHistory(history)
    }
    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(
            historyDao: HistoryDao
        ): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(historyDao)
            }.also { instance = it }
    }
}