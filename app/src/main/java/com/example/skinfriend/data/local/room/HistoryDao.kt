package com.example.skinfriend.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.entity.HistoryWithRecommendations
import com.example.skinfriend.data.local.entity.RecommendationEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getHistory(): LiveData<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(event: HistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendationItems(items: List<RecommendationEntity>)

    @Transaction
    @Query("SELECT * FROM history WHERE id = :historyId")
    suspend fun getHistoryWithRecommendations(historyId: Int): HistoryWithRecommendations

    @Transaction
    @Query("SELECT * FROM history WHERE id = :historyId")
    fun getHistoryWithRecommendationsLive(historyId: Int): LiveData<HistoryWithRecommendations>
}