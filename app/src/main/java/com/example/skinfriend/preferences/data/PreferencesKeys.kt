package com.example.skinfriend.preferences.data

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val STRESS_LEVEL_KEY = stringPreferencesKey("stress_level")
    val DATE_KEY = stringPreferencesKey("last_checked_date")
    val STREAK_KEY = intPreferencesKey("day_streak")
}