package com.example.skinfriend.di

import android.content.Context
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import com.example.skinfriend.data.repository.HistoryRepository
import com.example.skinfriend.data.repository.SkincareRepository

object Injection {
    fun provideSkincareRepository(context: Context): SkincareRepository {
        val apiService = ApiConfig.getApiService()
        return SkincareRepository.getInstance(apiService)
    }
}