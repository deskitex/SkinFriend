package com.example.skinfriend.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.skinfriend.data.local.entity.FavoriteEntity
import com.example.skinfriend.data.local.entity.HistoryEntity
import com.example.skinfriend.data.local.room.FavoriteDao
import com.example.skinfriend.data.local.room.HistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class FavoriteRepository private constructor(
    private val favoriteDao: FavoriteDao,
) {
    fun getFavorite(): LiveData<List<FavoriteEntity>> = favoriteDao.getFavorite()

    suspend fun insertFavorite(favorite: FavoriteEntity) {
        withContext(Dispatchers.IO) {
            favoriteDao.insertFavorite(favorite)
        }
    }

    suspend fun deleteFavorite(productName: String) {
        withContext(Dispatchers.IO) {
            favoriteDao.deleteFavorite(productName)
        }
    }

    suspend fun isEventFavoriteSync(productName: String): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteDao.isEventFavoriteSync(productName)
        }
    }

    companion object {
        @Volatile
        private var instance: FavoriteRepository? = null
        fun getInstance(
            favoriteDao: FavoriteDao
        ): FavoriteRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteRepository(favoriteDao)
            }.also { instance = it }
    }
}
