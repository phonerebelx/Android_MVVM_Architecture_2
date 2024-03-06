package com.example.meezan360.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


fun AppCompatActivity.handleErrorResponse(responseModel: ResponseModel.Error<*>) {
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    if (responseModel.message != null) {
        Log.d("res_message", responseModel.message)
    }

    val response = responseModel.data as Response<*>
    if (response.code() == 551) {

        val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        sharedPreferencesManager.clearSharedPreferences()
        val intent = Intent(this, LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    if (response.code() == 400) {
        val jsonString = response.errorBody()?.string().toString()
        try {
            val jsonObject = JSONObject(jsonString)
            val error: String = jsonObject.getString("error")
            Toast.makeText(
                applicationContext,
                "error: " + error.toString(),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

}


