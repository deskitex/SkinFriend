//package com.example.skinfriend.data.local.room
//
//import androidx.lifecycle.LiveData
//import androidx.room.*
//import com.example.skinfriend.data.local.entity.RecommendationEntity
//
//@Dao
//interface RecommendationDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertRecommendations(recommendations: List<RecommendationEntity>)
//
//    @Query("SELECT * FROM recommendations")
//    fun getAllRecommendations(): LiveData<List<RecommendationEntity>>
//
//    @Query("DELETE FROM recommendations")
//    suspend fun clearRecommendations()
//}
