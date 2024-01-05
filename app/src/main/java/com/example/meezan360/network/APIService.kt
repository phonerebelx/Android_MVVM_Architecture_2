package com.example.meezan360.network

import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import com.example.meezan360.model.reports.DepositObject
import com.google.gson.JsonElement
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

    @GET("360/CheckVersioning")
    suspend fun checkVersioning(
    ): Response<KPIModel>

    @GET("360/GetDashboardByKpi")
    suspend fun getDashboardByKpi(
        @Query("kpi_id") kpiId: String,
    ): Response<DashboardByKPIModel>

    @GET("360/GetFooterGraphs")
    suspend fun getFooterGraphs(
        @Query("kpi_id") kpiId: String,
        @Query("tag_name") tagName: String,
        @Query("card_id") cardId: String,
    ): Response<JsonElement>

    @GET("360/GetDepositDetails")
    suspend fun getDepositDetails(
    ): Response<DepositObject>
}