package com.example.meezan360.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.model.Error
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.ChangePasswordActivity.ChangePasswordActivity
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.activities.LoginScreen
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okio.use
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


fun <T> DockActivity.handleErrorResponse(responseModel: ResponseModel.Error<T>) {
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
    sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
    if (responseModel.message != null) {
        Log.d("res_message", responseModel.message)
        Log.d("res_message2", responseModel.data.toString())
    }

    val response = responseModel.data as? Response<*>
    if (response != null) {
        if (response.code() == 551 || response.code() == 401) {

            sharedPreferencesManager.clearSharedPreferences()
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            var errorMessage = "Unknown error occurred"

            try {
                val errorResponse = Gson().fromJson(response.errorBody()?.string()
                    , Error::class.java)
                errorMessage = errorResponse.error
            } catch (e: JsonSyntaxException) {
                // Handle invalid JSON format
                e.printStackTrace()
            }
            showErrorMessage(errorMessage)

        } else if (response.code() == 552) {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        } else if (response.code() == 500) {
            showErrorMessage("Internal Server Error")
        }


        if (response.code() == 400 || response.code() == 422) {
            val errorBodyString = response.errorBody()?.string().toString()

            try {
                val jsonObject = JSONObject(errorBodyString)
                val error: String = jsonObject.getString("error")

                showErrorMessage(error)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

}


