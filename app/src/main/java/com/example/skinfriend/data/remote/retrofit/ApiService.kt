package com.example.skinfriend.data.remote.retrofit

import com.example.skinfriend.data.remote.response.NewsResponse
import com.example.skinfriend.data.remote.response.SkincareResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): SkincareResponse

    @GET("v2/top-headlines")
    fun getNews(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}