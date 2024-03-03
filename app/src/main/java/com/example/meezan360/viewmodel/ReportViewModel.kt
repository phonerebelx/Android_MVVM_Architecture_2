package com.example.meezan360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.model.CardLevelModel.CardLevelDataModel
import com.example.meezan360.model.SearchFilterModel.SearchFilterDataModel
import com.example.meezan360.model.reports.DepositObject
import com.example.meezan360.model.reports.Level2ReportModel
import com.example.meezan360.network.ResponseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ReportViewModel(private var dataRepo: DataRepository?) : ViewModel() {

    val depositDetail = MutableStateFlow<ResponseModel<Response<DepositObject>>>(ResponseModel.Idle("Idle State"))

    val levelTwo = MutableStateFlow<ResponseModel<Response<ArrayList<Level2ReportModel>>>>(ResponseModel.Idle("Idle State"))

    val customerService = MutableStateFlow<ResponseModel<Response<CardLevelDataModel>>>(ResponseModel.Idle("Idle State"))


    suspend fun getDepositDetails() {
        depositDetail.emit(ResponseModel.Loading())
        dataRepo?.getDepositDetails()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) depositDetail.emit(ResponseModel.Success(it))
                else depositDetail.emit(ResponseModel.Error(it.message()))
            }
        }
    }

    suspend fun getLevelTwo(kpiId: String, tableId: String,identifierType: String,identifier: String) {
        levelTwo.emit(ResponseModel.Loading())
        dataRepo?.getLevelTwo(kpiId, tableId,identifierType,identifier)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) levelTwo.emit(ResponseModel.Success(it))
                else levelTwo.emit(ResponseModel.Error(it.message()))
            }
        }
    }

    suspend fun getCustomerService(cif_id: String) {
        customerService.emit(ResponseModel.Loading())
        dataRepo?.getCustomerService(cif_id)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) customerService.emit(ResponseModel.Success(it))
                else customerService.emit(ResponseModel.Error(it.message()))
            }
        }
    }




}