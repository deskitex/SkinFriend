package com.example.skinfriend.data.repository

import com.example.skinfriend.preferences.UserPreferences

class UserRepository private constructor(private val userPreferences: UserPreferences) {

    suspend fun getStressQuizResult(): String? {
        return userPreferences.getStressQuizResult()
    }

    suspend fun onUserStreak() {
        userPreferences.onUserStreak()
    }
    suspend fun getStreak(): Int {
        return userPreferences.getStreak()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreferences: UserPreferences): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userPreferences).also { instance = it }
            }
        }
    }
}

