package com.example.architecture.network



import com.example.architecture.model.LoginModel
import retrofit2.Response

import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {
    @POST("login")
    suspend fun loginRequest(
        @Query("login_id") loginId: String,
        @Query("password") password: String,
        @Query("device_id") deviceId: String,
    ): Response<LoginModel>

  }