package com.example.architecture.datamodule.repository
import com.example.architecture.di.NetworkModule
import com.example.architecture.model.LoginModel

import com.example.architecture.network.NoInternetException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

class DataRepository(private var networkModule: NetworkModule) {

    suspend fun getLoginRequest(
        loginId: String, password: String, deviceId: String
    ): Flow<Response<LoginModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().loginRequest(loginId, password, deviceId)
                emit(response)
            } catch (e: NoInternetException) {
             val errorResponseBody = ResponseBody.create(null, "Internet connection unavailable. Please connect to Wi-Fi or enable mobile data to proceed.")
            emit(Response.error(1000, errorResponseBody))
        } catch (e: IOException) {
                 val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }
}