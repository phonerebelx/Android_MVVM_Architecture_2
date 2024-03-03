package com.example.meezan360.network

import com.example.meezan360.model.CardLevelModel.CardLevelDataModel
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.SearchFilterModel.GetSetFilterModel.GetSetFilterDataResponseModel
import com.example.meezan360.model.SearchFilterModel.ResetFilter.ResetFilterResponseDataModel
import com.example.meezan360.model.SearchFilterModel.SearchFilterDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterRequestDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterResponseDataModel
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import com.example.meezan360.model.reports.DepositObject
import com.example.meezan360.model.reports.Level2ReportModel
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
        @Query("tag") tag: String,
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

    @GET("360/LevelTwo")
    suspend fun getLevelTwo(
        @Query("kpi_id") kpiId: String,
        @Query("table_id") tableId: String,
        @Query("identifier_type") identifier_type: String,
        @Query("identifier") identifier: String
    ): Response<ArrayList<Level2ReportModel>>

    @GET("360/lovs")
    suspend fun getLovs(
    ): Response<SearchFilterDataModel>

    @POST("360/GetSetFilter")
    suspend fun getSetFilter(
    ): Response<GetSetFilterDataResponseModel>


    @POST("360/SetFilter")
    suspend fun setFilter(
        @Query("selected_area") selected_area: String,
        @Query("selected_region") selected_region: String,
        @Query("selected_branch") selected_branch: String,
        @Query("selected_date") selected_date: String
    ): Response<SetFilterResponseDataModel>

    @POST("360/ResetFilter")
    suspend fun resetFilter(
    ): Response<ResetFilterResponseDataModel>


    @POST("360/GetCustomerService")
    suspend fun getCustomerService(
        @Query("response-type") response_type: String,
        @Query("cif_id") cif_id: String
    ): Response<CardLevelDataModel>
}