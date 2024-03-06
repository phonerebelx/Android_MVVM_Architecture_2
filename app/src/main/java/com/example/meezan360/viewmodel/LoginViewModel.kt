package com.example.meezan360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.adcarchitecture.model.otp.OtpModel
import com.app.adcarchitecture.model.otp.OtpResponse
import com.app.adcarchitecture.model.resetPassword.ResetPasswordModel
import com.app.adcarchitecture.model.resetPassword.ResetPwdReqResponse
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.model.LoginModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private var dataRepo: DataRepository?) : ViewModel() {

    val loginData = MutableStateFlow<ResponseModel<Response<LoginModel>>>(ResponseModel.Idle("Idle State"))
    val resetPassData = MutableStateFlow<ResponseModel<Response<ResetPwdReqResponse>>>(ResponseModel.Idle("Idle State"))
    val verifyOtpData = MutableStateFlow<ResponseModel<Response<OtpResponse>>>(ResponseModel.Idle("Idle State"))

    suspend fun loginRequest(loginId: String, password: String, deviceId: String) {

        loginData.emit(ResponseModel.Loading())
        dataRepo?.getLoginRequest(loginId, password, deviceId)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) loginData.emit(ResponseModel.Success(it))
                else loginData.emit(ResponseModel.Error(it.message()))
            }
        }
    }

    suspend fun resetPasswordRequest( resetPasswordModel: ResetPasswordModel) {
        resetPassData.emit(ResponseModel.Loading())
        dataRepo?.resetPasswordRequest(resetPasswordModel)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) resetPassData.emit(ResponseModel.Success(it))
                else resetPassData.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun verifyOtp(otpModel: OtpModel) {
        verifyOtpData.emit(ResponseModel.Loading())
        dataRepo?.verifyOtp(otpModel)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) verifyOtpData.emit(ResponseModel.Success(it))
                else verifyOtpData.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }
}