package com.example.meezan360.datamodule.local

import android.content.SharedPreferences

class SharedPreferencesManager(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_LOGIN_ID = "login_id"
    }

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }
    fun saveLoginId(loginId: String?) {
        sharedPreferences.edit().putString(KEY_LOGIN_ID, loginId).apply()
    }
    fun getLoginId(): String?  {
        return sharedPreferences.getString(KEY_LOGIN_ID, null)
    }



    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }


}