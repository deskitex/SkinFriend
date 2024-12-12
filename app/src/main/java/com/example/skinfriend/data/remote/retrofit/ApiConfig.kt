package com.example.skinfriend.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.skinfriend.BuildConfig

class ApiConfig {

    companion object {
        @Volatile
        private var retrofitInstance: ApiService? = null

        fun getAuthService(): ApiService {
            return retrofitInstance ?: synchronized(this) {
                val loggingInterceptor = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL_AUTH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                retrofitInstance = retrofit.create(ApiService::class.java)
                retrofitInstance!!
            }
        }

        fun getPredicttService(): ApiService {
            return retrofitInstance ?: synchronized(this) {
                val loggingInterceptor = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL_PREDICT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                retrofitInstance = retrofit.create(ApiService::class.java)
                retrofitInstance!!
            }
        }
    }
}