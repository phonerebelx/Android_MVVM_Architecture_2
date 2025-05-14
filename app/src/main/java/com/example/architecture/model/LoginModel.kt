package com.example.architecture.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    val token: String,
    @SerializedName("two_factor")
    val twoFactor: String,
    val user: UserModel
)