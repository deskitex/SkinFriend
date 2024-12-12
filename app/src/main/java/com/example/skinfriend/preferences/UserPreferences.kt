package com.example.skinfriend.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.skinfriend.preferences.data.PreferencesKeys
import com.example.skinfriend.preferences.data.PreferencesKeys.STRESS_LEVEL_KEY

import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences private constructor(context: Context) {

    private val dataStore = context.dataStore
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // Keys for DataStore
    private val LAST_LOGIN_DATE_KEY = stringPreferencesKey("last_login_date")
    private val STREAK_KEY = intPreferencesKey("streak")

    suspend fun onUserStreak() {
        val today = getCurrentDateString()
        val yesterday = getYesterdayDate()
        val lastLoginDay = getLastLoginDate() ?: yesterday

        if (lastLoginDay == today) return

        val quizLastDay = getLastQuizDate() ?: yesterday

        dataStore.edit { preferences ->
            when (lastLoginDay) {
                yesterday -> {
                    if (quizLastDay == yesterday) {
                        preferences[STRESS_LEVEL_KEY] = "-"
                    }
                    preferences[STREAK_KEY] = (preferences[STREAK_KEY] ?: 0) + 1
                }
                else -> {
                    preferences[STREAK_KEY] = 0
                    preferences[STRESS_LEVEL_KEY] = "-"
                }
            }
            preferences[LAST_LOGIN_DATE_KEY] = today
        }
    }



    fun getCurrentDateString(): String {
        return dateFormat.format(Date())
    }

    fun getYesterdayDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return dateFormat.format(calendar.time)
    }

    private suspend fun getLastLoginDate(): String? {
        val preferences = dataStore.data.first()
        return preferences[LAST_LOGIN_DATE_KEY]
    }

    suspend fun getStreak(): Int {
        val preferences = dataStore.data.first()
        return preferences[STREAK_KEY] ?: 3
    }


    suspend fun saveStressQuizResult(stressLevel: String) {
        val today = getCurrentDateString()
        dataStore.edit { preferences ->
            preferences[STRESS_LEVEL_KEY] = stressLevel
            preferences[LAST_QUIZ_DATE_KEY] = today
            Log.d("UserPreferences", "onUserStreak: ${dataStore.data.first()[LAST_QUIZ_DATE_KEY]}")

        }
    }

    // Tambahkan di UserPreferences
    private val STRESS_LEVEL_KEY = stringPreferencesKey("stress_level")
    private val LAST_QUIZ_DATE_KEY = stringPreferencesKey("last_quiz_date")



    suspend fun getStressQuizResult(): String? {
        val preferences = dataStore.data.first()
        return preferences[STRESS_LEVEL_KEY]
    }

    suspend fun getLastQuizDate(): String? {
        val preferences = dataStore.data.first()
        return preferences[LAST_QUIZ_DATE_KEY]
    }

    suspend fun updateLastQuizDate(date: String) {
        dataStore.edit { preferences ->
            preferences[LAST_QUIZ_DATE_KEY] = date
        }
    }

    companion object {
        @Volatile
        private var instance: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return instance ?: synchronized(this) {
                instance ?: UserPreferences(context).also { instance = it }
            }
        }
    }
}




