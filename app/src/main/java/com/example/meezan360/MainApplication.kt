package com.example.meezan360

import android.app.Application
import com.example.meezan360.datamodule.di.appModule
import com.example.meezan360.datamodule.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
    private fun initKoin() {
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, dataModule)
        }
    }
}