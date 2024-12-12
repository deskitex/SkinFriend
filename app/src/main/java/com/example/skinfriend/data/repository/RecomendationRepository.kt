package com.example.skinfriend.data.repository

import com.example.skinfriend.data.remote.response.SkincareResponse
import com.example.skinfriend.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RecomendationRepository private constructor(
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
        private var instance: RecomendationRepository? = null
        fun getInstance(
            apiService: ApiService,

            ): RecomendationRepository =
            instance ?: synchronized(this) {
                instance ?: RecomendationRepository(apiService)
            }.also { instance = it }
    }
}