package com.example.skinfriend.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.skinfriend.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getFavorite(): LiveData<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE productName = :productName")
    suspend fun deleteFavorite(productName: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE productName = :productName )")
    suspend fun isEventFavoriteSync(productName: String): Boolean

}