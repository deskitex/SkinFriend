package com.example.skinfriend.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.skinfriend.R
import com.example.skinfriend.data.remote.response.RecommendationsItem
import com.example.skinfriend.data.remote.response.SkincareResponse
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import com.example.skinfriend.data.remote.retrofit.ApiService
import com.example.skinfriend.ui.view.MainActivity.Companion.showToast
import com.example.skinfriend.util.reduceFileImage
import com.example.skinfriend.util.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class SkincareRepository private constructor(
    private val apiService: ApiService
    ) {

     suspend fun uploadImage(imageFile: File): SkincareResponse {
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                requestImageFile
            )
        return apiService.uploadImage(multipartBody)
    }

    companion object {
        const val TAG = "NewsRepository"

        @Volatile
        private var instance: SkincareRepository? = null
        fun getInstance(
            apiService: ApiService,

            ): SkincareRepository =
            instance ?: synchronized(this) {
                instance ?: SkincareRepository(apiService)
            }.also { instance = it }
    }
}