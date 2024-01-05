package com.example.meezan360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.model.reports.DepositObject
import com.example.meezan360.network.ResponseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ReportViewModel(private var dataRepo: DataRepository?) : ViewModel() {

    val depositDetail =
        MutableStateFlow<ResponseModel<Response<DepositObject>>>(ResponseModel.Idle("Idle State"))

    suspend fun getDepositDetails() {
        depositDetail.emit(ResponseModel.Loading())
        dataRepo?.getDepositDetails()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) depositDetail.emit(ResponseModel.Success(it))
                else depositDetail.emit(ResponseModel.Error(it.message()))
            }
        }
    }
}