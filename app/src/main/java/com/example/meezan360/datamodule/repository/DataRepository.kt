package com.example.meezan360.datamodule.repository

import com.example.meezan360.di.NetworkModule
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class DataRepository(private var networkModule: NetworkModule) {
    suspend fun getLoginRequest(
        loginId: String, password: String, deviceId: String
    ): Flow<Response<LoginModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().loginRequest(loginId, password, deviceId)
            emit(response)
        }
    }

    suspend fun getCheckVersioning(): Flow<Response<KPIModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().checkVersioning()
            emit(response)
        }
    }

    suspend fun getDashboardByKpi(kpiId: String): Flow<Response<DashboardByKPIModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getDashboardByKpi(kpiId)
            emit(response)
        }
    }
}