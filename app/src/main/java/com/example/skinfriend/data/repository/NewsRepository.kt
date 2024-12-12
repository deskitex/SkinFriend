package com.example.skinfriend.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skinfriend.BuildConfig
import com.example.skinfriend.data.remote.response.ArticlesItem
import com.example.skinfriend.data.remote.response.NewsResponse
import com.example.skinfriend.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository private constructor(
    private val apiService: ApiService,
) {
    private val _listNews = MutableLiveData<List<ArticlesItem>>()
    val listNews: LiveData<List<ArticlesItem>> = _listNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun showListNews() {
        _isLoading.value = true

        val client =
            apiService.getNews(
                query = "cancer",
                category = "health",
                language = "en",
                apiKey = BuildConfig.API_KEY
            )
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listNews.value = response.body()?.articles?.filter { it.author != null || it.urlToImage != null }
                    Log.d(TAG, "NewsSuccess: ${response.body()?.articles}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "NewsFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        const val TAG = "NewsRepository"

        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,

            ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService)
            }.also { instance = it }
    }
}