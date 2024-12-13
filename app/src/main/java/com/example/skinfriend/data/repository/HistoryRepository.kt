package com.example.skinfriend.data.repository

import androidx.lifecycle.LiveData
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.entity.HistoryWithRecommendations
import com.example.skinfriend.data.local.entity.RecommendationEntity
import com.example.skinfriend.data.local.room.HistoryDao

class HistoryRepository private constructor(
    private val historyDao: HistoryDao,
) {
    fun getHistory(): LiveData<List<HistoryEntity>> = historyDao.getHistory()

    suspend fun getHistoryItem(id: Int): HistoryWithRecommendations = historyDao.getHistoryWithRecommendations(id)

    // Menambahkan fungsi untuk mendapatkan LiveData dari HistoryWithRecommendations
    fun getHistoryWithRecommendationsLive(historyId: Int): LiveData<HistoryWithRecommendations> {
        return historyDao.getHistoryWithRecommendationsLive(historyId)
    }

    suspend fun insertHistory(history: HistoryEntity): Long {
        return historyDao.insertHistory(history)
    }

    suspend fun insertRecommendation(recommendation: List<RecommendationEntity>) {
        historyDao.insertRecommendationItems(recommendation)
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(historyDao: HistoryDao): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(historyDao)
            }.also { instance = it }
    }
}
