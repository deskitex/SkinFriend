package com.example.skinfriend.data.remote.response

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val noTelp: String,
    val gender: String,
)

data class RegisterResponse(

    @field:SerializedName("error")
    val error: Boolean?,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("userId")
    val userId: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

@Parcelize
data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class LoginResult(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null
) : Parcelable


