package com.example.architecture.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.architecture.datamodule.repository.ConnectivityRepository
import com.example.architecture.network.ResponseModel
import com.example.architecture.datamodule.repository.DataRepository
import com.example.architecture.interfaces.ApiListener
import com.example.architecture.model.LoginModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response

class LoginViewModel(private var dataRepo: DataRepository?,private val connectivityRepository: ConnectivityRepository) : ViewModel() {
    var apiListener: ApiListener? = null
    @RequiresApi(Build.VERSION_CODES.N)
    val isOnline = connectivityRepository.isConnected.asLiveData()
    val loginData = MutableStateFlow<ResponseModel<Response<LoginModel>>>(ResponseModel.Idle("Idle State"))



}