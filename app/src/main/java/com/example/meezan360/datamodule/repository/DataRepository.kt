package com.example.meezan360.datamodule.repository

import com.example.meezan360.di.NetworkModule
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import com.example.meezan360.model.reports.DepositObject
import com.example.meezan360.model.reports.Level2ReportModel
import com.google.gson.JsonElement
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

    suspend fun getFooterGraphs(
        kpiId: String,
        tagName: String,
        cardId: String,
    ): Flow<Response<JsonElement>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getFooterGraphs(kpiId, tagName, cardId)
            emit(response)
        }
    }

    suspend fun getDepositDetails(
    ): Flow<Response<DepositObject>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getDepositDetails()
            emit(response)
        }
    }

    suspend fun getLevelTwo(
        kpiId: String,
        tableId: String
    ): Flow<Response<Level2ReportModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getLevelTwo(kpiId, tableId)
            emit(response)
        }
    }
}