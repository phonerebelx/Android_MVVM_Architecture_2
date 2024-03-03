//package com.example.meezan360.datamodule.repository
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import com.example.meezan360.network.ResponseModel
//import okhttp3.ResponseBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.lang.Exception
//
//open class BaseRepository {
//
//    @Inject
//    lateinit var validationhelper: ValidationHelper
//    var apiResponse = MutableLiveData<String>()
//    var apiListener: ApiListener? = null
//
//    fun callApi(api: Call<ResponseBody>, tag: String): MutableLiveData<String> {
//        apiListener?.onStarted()
//        api.enqueue(object : Callback<ResponseBody> {
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                apiResponse.value = t.message
//                Log.i("xxFailure", t.message.toString())
//                apiListener?.onFailure(t.message!!, tag)
//            }
//
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful && (response.code() == 200)) {
//                    apiResponse.value = response.body()?.string()
//                    apiListener?.onSuccess(apiResponse, tag)
//                } else if (response.code() == 500) {
//                    apiListener?.onFailure("Internal Server Error", tag)
//                } else if(response.code() == 551 || response.code() == 401) {
//                    apiListener?.onFailureWithResponseCode(response.code(), "Force logout", tag)
//                } else if(response.code() == 552) {
//                    apiListener?.onFailureWithResponseCode(response.code(), "Change password", tag)
//                    validationhelper.navigateToChangePassword()
//                } else {
//                    try {
//                        apiResponse.value = response.errorBody()?.string()
//                        val errorResponseEnt = GsonFactory.getConfiguredGson()?.fromJson(apiResponse.value, ErrorResponseEnt::class.java)
//                        if (errorResponseEnt?.error != null) {
//                            apiListener?.onFailure(errorResponseEnt.error, tag)
//                        } else {
//                            apiListener?.onFailure("Something went wrong", tag)
//                        }
//                    }
//                    catch (e: Exception){
//                        apiListener?.onFailure("Something went wrong", tag)
//                    }
//                }
//            }
//        })
//        return apiResponse
//    }
//}