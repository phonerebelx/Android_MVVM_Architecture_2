package com.example.meezan360.di

import android.content.Context
import android.content.SharedPreferences
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.APIClient
import com.example.meezan360.network.APIService
import com.example.meezan360.datamodule.repository.DataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataModule = module {

    //for local data
    single<SharedPreferences> {
        androidContext().getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
    }
    single { SharedPreferencesManager(get()) }
    //

    single(named("data_repo")) { DataRepository(NetworkModule(get(),androidContext())) }
}

class NetworkModule(private val sharedPreferencesManager: SharedPreferencesManager,private val context: Context) {
    fun sourceOfNetwork(): APIService {
        return APIClient.create(sharedPreferencesManager, context = context)
    }
}