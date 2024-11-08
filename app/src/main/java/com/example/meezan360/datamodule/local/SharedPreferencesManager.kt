package com.example.meezan360.datamodule.local

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.example.meezan360.security.EncryptionKeyStoreImpl
import com.example.meezan360.utils.Constants
import com.google.gson.GsonBuilder
import com.uhfsolutions.sfts.model.fingerprintLoginData.FingerprintLoginData

class SharedPreferencesManager( @PublishedApi internal val sharedPreferences: SharedPreferences) {

    private val encryptionKeyStore = EncryptionKeyStoreImpl.instance

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_LOGIN_ID = "login_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_emaill"
        private const val KEY_VALUE_CALLED = "Value_called"
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
    fun saveUserName(userName: String?){
        sharedPreferences.edit().putString(KEY_USER_NAME, userName).apply()
    }
    fun saveUserEmail(userEmail: String?){
        sharedPreferences.edit().putString(KEY_USER_EMAIL, userEmail).apply()
    }
    fun getUserName(): String?  {
        return sharedPreferences.getString(KEY_USER_NAME, "")
    }
    fun getUserEmail(): String?  {
        return sharedPreferences.getString(KEY_USER_EMAIL, "")
    }


    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveValue(value: String) {

            sharedPreferences.edit().putString(KEY_VALUE_CALLED, value).apply()

    }
    fun getValue(): String? {
        return sharedPreferences.getString(KEY_VALUE_CALLED, null)
    }
//    KEY_VALUE_CALLED
    fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }
    fun <T> put(`object`: T, key: String) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(`object`)
        //Save that String in SharedPreferences
        sharedPreferences.edit().putString(key, jsonString).apply()
    }


    inline fun <reified T> get(key: String): T? {
        //We read JSON String which was saved.
        val value = sharedPreferences.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

    fun setFingerprintLoginData(user_data: FingerprintLoginData): Boolean {
        val fingerprintLoginData: FingerprintLoginData = user_data
        fingerprintLoginData.user_id = user_data.user_id
        fingerprintLoginData.password = user_data.password

        val editor = sharedPreferences.edit()
        put(fingerprintLoginData, Constants.FINGERPRINT_LOGIN_DATA)
        editor.commit()

        return true
    }
    // AMMAR - We take userid and password from shared prefs, decrypt it and then return it
    fun getFingerprintLoginData(): FingerprintLoginData {
        val fingerprintLoginData: FingerprintLoginData? = get<FingerprintLoginData>(Constants.FINGERPRINT_LOGIN_DATA)
        val userid = fingerprintLoginData!!.user_id
        val pass =  fingerprintLoginData.password
        return FingerprintLoginData(userid, pass)
    }


    @SuppressLint("CommitPrefEdits")
    fun logout(): Boolean {
        return if (get<Boolean>(Constants.IS_FINGERPRINT)!!) {
            return try {
                // AMMAR - Gets login data
                val fingerprintLoginData: FingerprintLoginData = getFingerprintLoginData()
                // AMMAR - Clears everything from shared prefs
                sharedPreferences.edit().clear().commit()
//                sharedPreferences.edit().commit()

                // AMMAR - Re-inserts login data into shared prefs
                setFingerprintLoginData(FingerprintLoginData(fingerprintLoginData.user_id, fingerprintLoginData.password))
                put(true, Constants.IS_FINGERPRINT)
                true
            } catch (e: Exception) {
                // AMMAR - Handles situation if no login data is found for any reason
                put(false, Constants.IS_FINGERPRINT)
                true
            }
        } else {
            // AMMAR - Clears everything from shared prefs
            sharedPreferences.edit().clear().commit()

            true
        }
    }

}