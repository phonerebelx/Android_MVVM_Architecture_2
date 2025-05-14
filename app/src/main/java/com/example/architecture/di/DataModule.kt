package com.example.architecture.di

import android.content.Context
import android.content.SharedPreferences
import com.example.architecture.datamodule.local.SharedPreferencesManager
import com.example.architecture.network.APIClient
import com.example.architecture.network.APIService
import com.example.architecture.datamodule.repository.DataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataModule = module {

    //for local data
    single<SharedPreferences> {
        androidContext().getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
    }
    single { SharedPreferencesManager(get()) }

    single(named("data_repo")) { DataRepository(NetworkModule(get(),androidContext())) }
}

class NetworkModule(private val sharedPreferencesManager: SharedPreferencesManager,private val context: Context) {
    fun sourceOfNetwork(): APIService {
        return APIClient.create(sharedPreferencesManager, context = context)
    }
}