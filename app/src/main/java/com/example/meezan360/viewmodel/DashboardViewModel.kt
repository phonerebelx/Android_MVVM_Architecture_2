package com.example.meezan360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import com.example.meezan360.network.ResponseModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardViewModel(private var dataRepo: DataRepository?) : ViewModel() {

    val footerGraph =
        MutableStateFlow<ResponseModel<Response<JsonElement>>>(ResponseModel.Idle("Idle State"))

    val dashboardByKPI =
        MutableStateFlow<ResponseModel<Response<DashboardByKPIModel>>>(ResponseModel.Idle("Idle State"))

    val checkVersioning = MutableStateFlow<ResponseModel<Response<KPIModel>>>(ResponseModel.Idle("Idle State"))

    suspend fun checkVersioning() {

        checkVersioning.emit(ResponseModel.Loading())
        dataRepo?.getCheckVersioning()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) checkVersioning.emit(ResponseModel.Success(it))
                else checkVersioning.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun getDashboardByKpi(kpiId: String,tag: String) {
        dashboardByKPI.emit(ResponseModel.Loading())
        dataRepo?.getDashboardByKpi(kpiId,tag)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) dashboardByKPI.emit(ResponseModel.Success(it))
                else dashboardByKPI.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun getFooterGraphs(kpiId: String, tagName: String, cardId: String) {
        footerGraph.emit(ResponseModel.Loading())
        dataRepo?.getFooterGraphs(kpiId, tagName, cardId)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) footerGraph.emit(ResponseModel.Success(it))
                else footerGraph.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

}