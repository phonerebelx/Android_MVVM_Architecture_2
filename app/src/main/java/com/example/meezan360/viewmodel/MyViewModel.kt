package com.example.meezan360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class MyViewModel(private var dataRepo: DataRepository?) : ViewModel() {

    val loginData =
        MutableStateFlow<ResponseModel<Response<LoginModel>>>(ResponseModel.Idle("Idle State"))

    val checkVersioning =
        MutableStateFlow<ResponseModel<Response<KPIModel>>>(ResponseModel.Idle("Idle State"))

    suspend fun loginRequest(loginId: String, password: String, deviceId: String) {
        loginData.emit(ResponseModel.Loading())
        dataRepo?.getLoginRequest(loginId, password, deviceId)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) loginData.emit(ResponseModel.Success(it))
                else loginData.emit(ResponseModel.Error(it.message()))
            }
        }
    }

    suspend fun checkVersioning() {
        checkVersioning.emit(ResponseModel.Loading())
        dataRepo?.getCheckVersioning()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) checkVersioning.emit(ResponseModel.Success(it))
                else checkVersioning.emit(ResponseModel.Error(it.message()))
            }
        }
    }

}