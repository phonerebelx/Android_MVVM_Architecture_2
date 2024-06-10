package com.example.meezan360.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.adcarchitecture.model.otp.OtpModel
import com.app.adcarchitecture.model.otp.OtpResponse
import com.example.meezan360.datamodule.repository.ConnectivityRepository
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.interfaces.ApiListener
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.changePassword.VerifyPassModel
import com.example.meezan360.model.changePassword.VerifyPassResponse
import com.example.meezan360.model.changePassword.VerifyPwdReqResponse
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.model.changenewpassword.ChangePasswordResponse
import com.example.meezan360.model.logout.LogoutResponse
import com.example.meezan360.model.resetPassword.ResetPasswordModel
import com.example.meezan360.model.resetPassword.ResetPwdReqResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
class LoginViewModel(private var dataRepo: DataRepository?,private val connectivityRepository: ConnectivityRepository) : ViewModel() {
    var apiListener: ApiListener? = null
    @RequiresApi(Build.VERSION_CODES.N)
    val isOnline = connectivityRepository.isConnected.asLiveData()
    val loginData = MutableStateFlow<ResponseModel<Response<LoginModel>>>(ResponseModel.Idle("Idle State"))
    val resetPassData = MutableStateFlow<ResponseModel<Response<ResetPwdReqResponse>>>(ResponseModel.Idle("Idle State"))
    val verifyOtpData = MutableStateFlow<ResponseModel<Response<OtpResponse>>>(ResponseModel.Idle("Idle State"))
    val changePasswordpData = MutableStateFlow<ResponseModel<ChangePasswordResponse>>(ResponseModel.Idle("Idle State"))
    val verifyPasswordData = MutableStateFlow<ResponseModel<VerifyPassResponse>>(ResponseModel.Idle("Idle State"))
    val logoutData = MutableStateFlow<ResponseModel<Response<LogoutResponse>>>(ResponseModel.Idle("Idle State"))


    suspend fun loginRequest(loginId: String, password: String, deviceId: String) {

        loginData.emit(ResponseModel.Loading())
        dataRepo?.getLoginRequest(loginId, password, deviceId)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) loginData.emit(ResponseModel.Success(it))
                else loginData.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun resetPasswordRequest(resetPasswordModel: ResetPasswordModel) {
        resetPassData.emit(ResponseModel.Loading())
        dataRepo?.resetPasswordRequest(resetPasswordModel)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) resetPassData.emit(ResponseModel.Success(it))
                else resetPassData.emit(ResponseModel.Error(it.message(), it))
            }
        }
    }

    suspend fun verifyOtp(otpModel: OtpModel) {
        verifyOtpData.emit(ResponseModel.Loading())
        dataRepo?.verifyOtp(otpModel)?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) verifyOtpData.emit(ResponseModel.Success(it))
                else verifyOtpData.emit(ResponseModel.Error(it.message(), it))
            }
        }
    }

    suspend fun changePassword(login_id: String,
                               new_password_confirmation: String,
                               new_password: String,
                               old_password: String) {

        changePasswordpData.emit(ResponseModel.Loading())
        var apiResponse = MutableLiveData<String>()
        dataRepo?.changePassword(login_id,new_password_confirmation,new_password,old_password)?.collect { response ->
//            viewModelScope.launch {
//                if (it.isSuccessful) changePasswordpData.emit(ResponseModel.Success(it))
//                else changePasswordpData.emit(ResponseModel.Error(it.message(), it))
//            }
            response.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    apiResponse.value = t.message

                    viewModelScope.launch {
                        verifyPasswordData.emit(ResponseModel.Error(apiResponse.value))
                    }
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && (response.code() == 200)) {
                        viewModelScope.launch {
                            val responseBody = response.body()?.string()
                            if (!responseBody.isNullOrEmpty()) {
                                val changePasswordResponse: ChangePasswordResponse? = Gson().fromJson(
                                    responseBody,
                                    ChangePasswordResponse::class.java
                                )
                                if (changePasswordResponse != null) {
                                    changePasswordpData.emit(ResponseModel.Success(changePasswordResponse))
                                } else {
                                    changePasswordpData.emit(ResponseModel.Error("Failed to parse response"))
                                }
                            } else {
                                changePasswordpData.emit(ResponseModel.Error("Empty response body"))
                            }
                        }
                    } else if (
                         response.code() == 400
                        ||  response.code() == 422) {
                        val responseBody = response.errorBody()?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            val changePasswordResponse: ChangePasswordResponse? = Gson().fromJson(
                                responseBody,
                                ChangePasswordResponse::class.java
                            )
                            viewModelScope.launch {
                                if (changePasswordResponse != null) {
                                    apiListener?.onFailure(changePasswordResponse.error,"Verify_Password_Data")
                                }

                            }
                        }

                    }else if (response.code() == 500) {
                        apiListener?.onFailure("Internal Server Error", "onFailure_Change_Password")
                    } else if(response.code() == 551) {
                        apiListener?.onFailureWithResponseCode(response.code(), "Force logout", "onFailureWithResponseCode_551")

                    } else if(response.code() == 552) {
                        apiListener?.onFailureWithResponseCode(response.code(), "Change password", "onFailureWithResponseCode_552")

                    }

                    else {
                        viewModelScope.launch {
                            changePasswordpData.emit(ResponseModel.Error("Response not successful"))
                        }
                    }
                }
            })
        }
    }



    suspend fun resetPasswordVerify(verifyPassModel: VerifyPassModel) {
        verifyPasswordData.emit(ResponseModel.Loading())
        var apiResponse = MutableLiveData<String>()
        dataRepo?.resetPasswordVerify(verifyPassModel)?.collect { response ->

            // You are already within a coroutine scope here
            response.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    apiResponse.value = t.message

                    viewModelScope.launch {
                        verifyPasswordData.emit(ResponseModel.Error(apiResponse.value))
                    }
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && (response.code() == 200)) {
                        viewModelScope.launch {
                            val responseBody = response.body()?.string()
                            if (!responseBody.isNullOrEmpty()) {
                                val verifyPassResponse: VerifyPassResponse? = Gson().fromJson(
                                    responseBody,
                                    VerifyPassResponse::class.java
                                )
                                if (verifyPassResponse != null) {
                                    verifyPasswordData.emit(ResponseModel.Success(verifyPassResponse))
                                } else {
                                    verifyPasswordData.emit(ResponseModel.Error("Failed to parse response"))
                                }
                            } else {
                                verifyPasswordData.emit(ResponseModel.Error("Empty response body"))
                            }
                        }
                    }  else if (
                        response.code() == 400
                        ||  response.code() == 422) {
                        val responseBody = response.errorBody()?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            val verifyPassResponse: VerifyPassResponse? = Gson().fromJson(
                                responseBody,
                                VerifyPassResponse::class.java
                            )
                            viewModelScope.launch {
                                if (verifyPassResponse != null) {
                                    apiListener?.onFailure(verifyPassResponse.error,"Verify_Password_Data")
                                }

                            }
                        }

                    }else if (response.code() == 500) {
                        apiListener?.onFailure("Internal Server Error", "onFailure_Change_Password")
                    } else if(response.code() == 551) {
                        apiListener?.onFailureWithResponseCode(response.code(), "Force logout", "onFailureWithResponseCode_551")

                    } else if(response.code() == 552) {
                        apiListener?.onFailureWithResponseCode(response.code(), "Change password", "onFailureWithResponseCode_552")

                    }
                    else {
                        viewModelScope.launch {
                            verifyPasswordData.emit(ResponseModel.Error("Response not successful"))
                        }
                    }
                }
            })
        }
    }

    suspend fun logoutRequest() {
        logoutData.emit(ResponseModel.Loading())
        dataRepo?.logoutRequest()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) logoutData.emit(ResponseModel.Success(it))
                else logoutData.emit(ResponseModel.Error(it.message(), it))
            }
        }
    }
}