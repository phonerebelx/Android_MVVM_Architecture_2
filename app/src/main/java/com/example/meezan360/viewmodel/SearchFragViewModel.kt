package com.example.meezan360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.model.SearchFilterModel.GetSetFilterModel.GetSetFilterDataResponseModel
import com.example.meezan360.model.SearchFilterModel.ResetFilter.ResetFilterResponseDataModel
import com.example.meezan360.model.SearchFilterModel.SearchFilterDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterRequestDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterResponseDataModel
import com.example.meezan360.network.ResponseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchFragViewModel(private var dataRepo: DataRepository?) : ViewModel()  {

    val getLovResponse = MutableStateFlow<ResponseModel<Response<SearchFilterDataModel>>>(
        ResponseModel.Idle("Idle State"))
    val getSetFilterResponse = MutableStateFlow<ResponseModel<Response<GetSetFilterDataResponseModel>>>(
        ResponseModel.Idle("Idle State"))
    val setFilterResponse = MutableStateFlow<ResponseModel<Response<SetFilterResponseDataModel>>>(
        ResponseModel.Idle("Idle State"))

    val resetFilterResponse = MutableStateFlow<ResponseModel<Response<ResetFilterResponseDataModel>>>(
        ResponseModel.Idle("Idle State"))
    suspend fun getLovs() {
        getLovResponse.emit(ResponseModel.Loading())
        dataRepo?.getLovs()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) getLovResponse.emit(ResponseModel.Success(it))
                else getLovResponse.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun getSetFilter() {
        getSetFilterResponse.emit(ResponseModel.Loading())
        dataRepo?.getSetFilter()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) getSetFilterResponse.emit(ResponseModel.Success(it))
                else getSetFilterResponse.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun resetFilter() {
        resetFilterResponse.emit(ResponseModel.Loading())
        dataRepo?.resetFilter()?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) resetFilterResponse.emit(ResponseModel.Success(it))
                else resetFilterResponse.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }

    suspend fun setFilter( selected_area: String, selected_region: String, selected_branch: String, selected_date: String) {
        setFilterResponse.emit(ResponseModel.Loading())
        dataRepo?.setFilter(
            selected_area,
            selected_region,
            selected_branch,
            selected_date
        )?.collect {
            viewModelScope.launch {
                if (it.isSuccessful) setFilterResponse.emit(ResponseModel.Success(it))
                else setFilterResponse.emit(ResponseModel.Error(it.message(),it))
            }
        }
    }
}