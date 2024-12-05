package com.example.skinfriend.ui.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skinfriend.data.remote.response.Predictions
import com.example.skinfriend.data.remote.response.RecommendationsItem
import com.example.skinfriend.data.repository.HistoryRepository
import com.example.skinfriend.data.repository.SkincareRepository
import com.example.skinfriend.util.reduceFileImage
import com.example.skinfriend.util.uriToFile
import kotlinx.coroutines.launch

class CameraViewModel(private val repository: SkincareRepository) : ViewModel() {

    private val _predictionResult = MutableLiveData<Predictions>()
    val predictionResult: LiveData<Predictions> = _predictionResult

    private val _recommendationResult = MutableLiveData<List<RecommendationsItem>>()
    val recommendationResult: LiveData<List<RecommendationsItem>> = _recommendationResult

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun uploadImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                // Ubah URI ke file
                val imageFile = uriToFile(uri, context).reduceFileImage()

                // Upload gambar ke repository
                val response = repository.uploadImage(imageFile)

                // Kirim hasil ke LiveData
                _predictionResult.value = response.predictions
                _recommendationResult.value = response.recommendations
                Log.d("SkincareViewModel", "Response: $response")
                Log.d("SkincareViewModel", "Predictions: ${response.predictions}")
                Log.d("SkincareViewModel", "Recommendations: ${response.recommendations}")
            } catch (e: Exception) {
                Log.e("SkincareViewModel", "Error uploading image: ${e.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }
}