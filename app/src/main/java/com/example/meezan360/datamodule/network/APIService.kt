package com.example.meezan360.datamodule.network

import com.example.meezan360.model.LoginModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
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