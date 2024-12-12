package com.example.skinfriend.ui.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skinfriend.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stressResult = MutableLiveData<String?>()
    val stressResult: LiveData<String?> = _stressResult

    private val _dayStreak = MutableLiveData<Int>()
    val dayStreak: LiveData<Int> = _dayStreak

    fun loadStressResult() {
        viewModelScope.launch {
            _stressResult.value = userRepository.getStressQuizResult()
        }
    }

    fun loadDayStreak() {
        viewModelScope.launch {
            _dayStreak.value = userRepository.getStreak()
            Log.d("HomeViewModel", "Streak Load: ${userRepository.getStreak()}")
        }
    }

    fun checkAndUpdateUserStreak() {
        viewModelScope.launch {
            userRepository.onUserStreak()
            Log.d("HomeViewModel", "Streak Check: ${userRepository.getStreak()}")
        }
    }
}

