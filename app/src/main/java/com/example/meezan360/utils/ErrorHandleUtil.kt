package com.example.meezan360.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.ChangePasswordActivity.ChangePasswordActivity
import com.example.meezan360.ui.activities.LoginScreen
import okio.use
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


fun <T> AppCompatActivity.handleErrorResponse(responseModel: ResponseModel.Error<T>) {
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
    sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
    if (responseModel.message != null) {
        Log.d("res_message", responseModel.message)
        Log.d("res_message2", responseModel.data.toString())
//        Toast.makeText(applicationContext, "responseModel.message", Toast.LENGTH_SHORT).show()
    }

    val response = responseModel.data as? Response<*>
    if (response != null) {
        if (response.code() == 551 || response.code() == 401) {


            sharedPreferencesManager.clearSharedPreferences()
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)


        } else if (response.code() == 552) {
//            change password will be done later first create main activity fragments as main fragment and then create nav graph

//            this scenario is worng and will change later
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }else if (response.code() == 500) {
            Toast.makeText(applicationContext, "error: Internal Server Error", Toast.LENGTH_SHORT).show()
        }


        if (response.code() == 400 || response.code() == 422) {
            val errorBodyString = response.errorBody()?.string().toString()

            try {
                val jsonObject = JSONObject(errorBodyString)
                val error: String = jsonObject.getString("error")
                Toast.makeText(applicationContext, "error: $error", Toast.LENGTH_SHORT).show()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

}


