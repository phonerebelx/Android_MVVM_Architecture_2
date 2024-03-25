package com.example.meezan360.datamodule.repository


import com.app.adcarchitecture.model.otp.OtpModel
import com.app.adcarchitecture.model.otp.OtpResponse
import com.example.meezan360.di.NetworkModule
import com.example.meezan360.model.CardLevelModel.CardLevelDataModel
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.SearchFilterModel.GetSetFilterModel.GetSetFilterDataResponseModel
import com.example.meezan360.model.SearchFilterModel.ResetFilter.ResetFilterResponseDataModel
import com.example.meezan360.model.SearchFilterModel.SearchFilterDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterResponseDataModel
import com.example.meezan360.model.changePassword.VerifyPassModel
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.model.changenewpassword.ChangePasswordResponse
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import com.example.meezan360.model.reports.DepositObject
import com.example.meezan360.model.reports.Level2ReportModel
import com.example.meezan360.model.resetPassword.ResetPasswordModel
import com.example.meezan360.model.resetPassword.ResetPwdReqResponse
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class DataRepository(private var networkModule: NetworkModule) {
    suspend fun getLoginRequest(
        loginId: String, password: String, deviceId: String
    ): Flow<Response<LoginModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().loginRequest(loginId, password, deviceId)
            emit(response)
        }
    }

    suspend fun getCheckVersioning(): Flow<Response<KPIModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().checkVersioning()
            emit(response)
        }
    }

    suspend fun getDashboardByKpi(kpiId: String,tag: String): Flow<Response<DashboardByKPIModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getDashboardByKpi(kpiId,tag)
            emit(response)
        }
    }

    suspend fun getFooterGraphs(
        kpiId: String,
        tagName: String,
        cardId: String,
    ): Flow<Response<JsonElement>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getFooterGraphs(kpiId, tagName, cardId)
            emit(response)
        }
    }

    suspend fun getDepositDetails(
    ): Flow<Response<DepositObject>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getDepositDetails()
            emit(response)
        }
    }

    suspend fun getLevelTwo(
        kpiId: String,
        tableId: String,
        identifierType: String,
        identifier: String
    ): Flow<Response<ArrayList<Level2ReportModel>>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getLevelTwo(kpiId, tableId,identifierType,identifier)
            emit(response)
        }
    }

    suspend fun getLovs(): Flow<Response<SearchFilterDataModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getLovs()
            emit(response)
        }
    }

    suspend fun getSetFilter(): Flow<Response<GetSetFilterDataResponseModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getSetFilter()
            emit(response)
        }
    }

    suspend fun resetFilter(): Flow<Response<ResetFilterResponseDataModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().resetFilter()
            emit(response)
        }
    }

    suspend fun setFilter(
        selected_area: String,
        selected_region: String,
        selected_branch: String,
        selected_date: String,
    ): Flow<Response<SetFilterResponseDataModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().setFilter(selected_area, selected_region, selected_branch, selected_date)
            emit(response)
        }
    }

    suspend fun getCustomerService(
        cif_id: String
    ): Flow<Response<CardLevelDataModel>> {
        return flow {
            val response = networkModule.sourceOfNetwork().getCustomerService("1",cif_id)
            emit(response)
        }
    }
    suspend fun resetPasswordRequest(
        resetPasswordModel: ResetPasswordModel
    ): Flow<Response<ResetPwdReqResponse>> {
        return flow {
            val response = networkModule.sourceOfNetwork().resetPasswordRequest(resetPasswordModel)
            emit(response)
        }
    }

    suspend fun verifyOtp(
        otpModel: OtpModel
    ): Flow<Response<OtpResponse>> {
        return flow {
            val response = networkModule.sourceOfNetwork().verifyOtp(otpModel)
            emit(response)
        }
    }
    suspend fun changePassword(
        changePasswordModel: ChangePasswordModel
    ): Flow<Response<ChangePasswordResponse>> {
        return flow {
            val response = networkModule.sourceOfNetwork().changePassword(changePasswordModel)
            emit(response)
        }
    }
    suspend fun resetPasswordVerify(
        verifyPassModel: VerifyPassModel
    ): Flow<Call<ResponseBody>> {
        return flow {
           val response = networkModule.sourceOfNetwork().verifyPassword(verifyPassModel)
            emit(response)
        }
    }

}