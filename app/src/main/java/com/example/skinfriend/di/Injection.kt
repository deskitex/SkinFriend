package com.example.skinfriend.di

import android.content.Context
import com.example.skinfriend.data.local.room.FavoriteDatabase
import com.example.skinfriend.data.local.room.HistoryDatabase
import com.example.skinfriend.preferences.UserPreferences
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import com.example.skinfriend.data.repository.FavoriteRepository
import com.example.skinfriend.data.repository.HistoryRepository
import com.example.skinfriend.data.repository.NewsRepository
import com.example.skinfriend.data.repository.RecomendationRepository
import com.example.skinfriend.data.repository.UserRepository

object Injection {
    fun provideRecommendationRepository(): RecomendationRepository {
        val apiService = ApiConfig.getApiServiceML()
        return RecomendationRepository.getInstance(apiService)
    }
    fun provideHistoryRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        return HistoryRepository.getInstance(dao)
    }
    fun provideFavoriteRepository(context: Context): FavoriteRepository {
        val database = FavoriteDatabase.getInstance(context)
        val dao = database.favoriteDao()
        return FavoriteRepository.getInstance(dao)
    }
    fun provideNewsRepository(context: Context): NewsRepository {
        val apiService = ApiConfig.getApiServiceNews()
        return NewsRepository.getInstance(apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val userPreferences = UserPreferences.getInstance(context)
        return UserRepository.getInstance(userPreferences)
    }
}