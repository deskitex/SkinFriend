package com.example.skinfriend.data.remote.retrofit

import com.example.skinfriend.data.remote.response.LoginRequest
import com.example.skinfriend.data.remote.response.LoginResponse
import com.example.skinfriend.data.remote.response.RegisterRequest
import com.example.skinfriend.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

}