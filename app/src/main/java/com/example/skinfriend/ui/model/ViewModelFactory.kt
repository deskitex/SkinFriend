package com.example.skinfriend.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skinfriend.di.Injection

class ViewModelFactory private constructor(
    private val creators: Map<Class<out ViewModel>, () -> ViewModel>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        return try {
            creator.invoke() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    mapOf(
                        CameraViewModel::class.java to { CameraViewModel(Injection.provideSkincareRepository(context)) }
                    )
                )
            }.also { instance = it }
    }
}