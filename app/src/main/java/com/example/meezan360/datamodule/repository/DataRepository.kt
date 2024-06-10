package com.example.meezan360.datamodule.repository


import android.util.Log
import com.app.adcarchitecture.model.otp.OtpModel
import com.app.adcarchitecture.model.otp.OtpResponse
import com.example.meezan360.di.NetworkModule
import com.example.meezan360.model.CardLevelModel.GetCardLevelDataModel
import com.example.meezan360.model.KPIModel
import com.example.meezan360.model.LoginModel
import com.example.meezan360.model.SearchFilterModel.GetSetFilterModel.GetSetFilterDataResponseModel
import com.example.meezan360.model.SearchFilterModel.ResetFilter.ResetFilterResponseDataModel
import com.example.meezan360.model.SearchFilterModel.SearchFilterDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterResponseDataModel
import com.example.meezan360.model.changePassword.VerifyPassModel
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.model.dashboardByKpi.DashboardByKPIModel
import com.example.meezan360.model.logout.LogoutResponse
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
import java.io.IOException

class DataRepository(private var networkModule: NetworkModule) {

    suspend fun getLoginRequest(
        loginId: String, password: String, deviceId: String
    ): Flow<Response<LoginModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().loginRequest(loginId, password, deviceId)
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }



        }
    }

    suspend fun getCheckVersioning(): Flow<Response<KPIModel>> {

        return flow {
            try {
                val response = networkModule.sourceOfNetwork().checkVersioning()
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }

        }
    }

    suspend fun getDashboardByKpi(kpiId: String, tag: String): Flow<Response<DashboardByKPIModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().getDashboardByKpi(kpiId, tag)
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun getFooterGraphs(
        kpiId: String,
        tagName: String,
        cardId: String,
    ): Flow<Response<JsonElement>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().getFooterGraphs(kpiId, tagName, cardId)
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun getDepositDetails(
    ): Flow<Response<DepositObject>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().getDepositDetails()
                emit(response)

            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun getLevelTwo(
        kpiId: String,
        tableId: String,
        identifierType: String,
        identifier: String
    ): Flow<Response<ArrayList<Level2ReportModel>>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork()
                    .getLevelTwo(kpiId, tableId, identifierType, identifier)
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun getLovs(): Flow<Response<SearchFilterDataModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().getLovs()
                emit(response)

            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun getSetFilter(): Flow<Response<GetSetFilterDataResponseModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().getSetFilter()
                emit(response)

            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun resetFilter(): Flow<Response<ResetFilterResponseDataModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().resetFilter()
                emit(response)

            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun setFilter(
        selected_area: String,
        selected_region: String,
        selected_branch: String,
        selected_date: String,
    ): Flow<Response<SetFilterResponseDataModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork()
                    .setFilter(selected_area, selected_region, selected_branch, selected_date)
                emit(response)

            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun getCustomerService(
        cif_id: String
    ): Flow<Response<GetCardLevelDataModel>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().getCustomerService("1", cif_id)
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun resetPasswordRequest(
        resetPasswordModel: ResetPasswordModel
    ): Flow<Response<ResetPwdReqResponse>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().resetPasswordRequest(resetPasswordModel)
                emit(response)
            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun verifyOtp(
        otpModel: OtpModel
    ): Flow<Response<OtpResponse>> {
        return flow {
            try {
                val response = networkModule.sourceOfNetwork().verifyOtp(otpModel)
                emit(response)

            } catch (e: IOException) {
                // Handle the IOException here
                val errorResponseBody = ResponseBody.create(null, "Unable to resolve host")
                emit(Response.error(500, errorResponseBody))
            }
        }
    }

    suspend fun changePassword(
         login_id: String,
         new_password_confirmation: String,
         new_password: String,
         old_password: String,
         prefix: String = "360"
//        changePasswordModel: ChangePasswordModel
    ): Flow<Call<ResponseBody>> {
        return flow {
            val response = networkModule.sourceOfNetwork().changePassword(login_id,new_password_confirmation,new_password,old_password,prefix)
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
    suspend fun logoutRequest(): Flow<Response<LogoutResponse>> {
        return flow {
            val response = networkModule.sourceOfNetwork().logoutRequest()
            emit(response)
        }
    }

}