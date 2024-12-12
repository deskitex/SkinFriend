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
import com.example.skinfriend.data.repository.RecomendationRepository
import com.example.skinfriend.util.reduceFileImage
import com.example.skinfriend.util.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class RecomendationViewModel(private val repository: RecomendationRepository) : ViewModel() {

    private val _predictionResult = MutableLiveData<Predictions>()
    val predictionResult: LiveData<Predictions> = _predictionResult

    private val _recommendationResult = MutableLiveData<List<RecommendationsItem>>()
    val recommendationResult: LiveData<List<RecommendationsItem>> = _recommendationResult

    private val _skintypeResult = MutableLiveData<List<String>>()
    val skintypeResult: LiveData<List<String >> = _skintypeResult

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    private suspend fun processImageBeforeUpload(uri: Uri, context: Context): File {
        return withContext(Dispatchers.IO) {
            val imageFile = uriToFile(uri, context).reduceFileImage()
            imageFile
        }
    }

    fun uploadImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                // Ubah URI ke file
                val imageFile = processImageBeforeUpload(uri, context)

                // Upload gambar ke repository
                val response = repository.uploadImage(imageFile)

                // Kirim hasil ke LiveData
                _predictionResult.value = response.predictions
                _recommendationResult.value = response.recommendations
                    .filter { isUrlValid(it.pictureSrc) }
                    .take(3)
                Log.d("SkincareViewModel", "Response: ${response.recommendations}")
                _skintypeResult.value = response.skinTypes

            } catch (e: Exception) {
                _loadingState.value = false
                Log.e("SkincareViewModel", "Error uploading image: ${e.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }

    private suspend fun isUrlValid(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET" // Gunakan GET daripada HEAD
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                // Baca beberapa byte saja untuk memverifikasi tanpa mengunduh sepenuhnya
                val inputStream = connection.inputStream
                val buffer = ByteArray(10)
                val bytesRead = inputStream.read(buffer)

                // Tutup stream dan koneksi
                inputStream.close()
                connection.disconnect()

                // Jika membaca byte berhasil, maka URL valid
                bytesRead > 0
            } catch (e: Exception) {
                false
            }
        }
    }
}