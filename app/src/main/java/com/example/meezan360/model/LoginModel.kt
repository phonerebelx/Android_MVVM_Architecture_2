package com.example.meezan360.model

data class LoginModel(
    val token: String,
    val two_factor: String,
    val user: User
)