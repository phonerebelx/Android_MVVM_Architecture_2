package com.example.meezan360.datamodule.local

import android.content.SharedPreferences

class SharedPreferencesManager(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_TOKEN = "token"
    }

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }


}