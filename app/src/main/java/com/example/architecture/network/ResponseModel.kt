package com.example.architecture.network

import android.content.Context
import android.content.Intent
import com.example.architecture.datamodule.local.SharedPreferencesManager
import com.example.architecture.ui.activities.LoginScreen
import retrofit2.Response

sealed class ResponseModel<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T> (data: T) : ResponseModel<T>(data) {


    }

    class Error<T>(message: String?, data: T? = null ) : ResponseModel<T>(data, message) {
        fun handleError(context: Context, sharedPreferencesManager: SharedPreferencesManager){
            val response = data as? Response<*>
            if (response?.code() == 551){
                sharedPreferencesManager.clearSharedPreferences()
                val intent = Intent(context, LoginScreen::class.java)
                context.startActivity(intent)
            }
        }
    }

    class Loading<T> : ResponseModel<T>()

    class Idle<T>(message: String?) : ResponseModel<T>(null, message)
}
