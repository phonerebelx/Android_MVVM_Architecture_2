package com.example.meezan360.network


import com.example.meezan360.datamodule.local.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient() {
    companion object {
        private const val BASE_URL = "http://thesalesforceapi.avengers.pk/api/v1/"
        fun create(sharedPreferencesManager: SharedPreferencesManager): APIService {

            val client = OkHttpClient.Builder().apply {
                addInterceptor(BaseHeadersInterceptor(sharedPreferencesManager))
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }.build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
    }
}

class BaseHeadersInterceptor(private val sharedPreferencesManager: SharedPreferencesManager) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            val token = sharedPreferencesManager.getToken()
            if (!token.isNullOrBlank()) {
                header("Authorization", "Bearer $token")
                header("response-type", "0")
            }
        }.build()
        return chain.proceed(request)
    }
}