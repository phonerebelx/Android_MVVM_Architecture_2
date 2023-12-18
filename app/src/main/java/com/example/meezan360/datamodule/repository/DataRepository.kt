package com.example.meezan360.datamodule.repository

import com.example.meezan360.datamodule.di.NetworkModule
import com.example.meezan360.model.LoginModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class DataRepository(private var networkModule: NetworkModule) {
    suspend fun getLoginRequest(
        loginId: String,
        password: String,
        deviceId: String
    ): Flow<Response<LoginModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().loginRequest(loginId, password, deviceId)
            emit(response)
        }
    }
}