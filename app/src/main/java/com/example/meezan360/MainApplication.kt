package com.example.meezan360

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.meezan360.di.appModule
import com.example.meezan360.di.dataModule
import com.example.meezan360.utils.InternetHelper
import com.scottyab.rootbeer.RootBeer
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    lateinit var rootBeer: RootBeer
    lateinit var internetHelper: InternetHelper


    override fun onCreate() {
        super.onCreate()
        rootBeer = RootBeer(applicationContext)
        internetHelper = InternetHelper(applicationContext)

            initKoin()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (rootBeer.isRooted) {
                    activity.finish()
                }else{

                    changeStatusBarColor(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })


    }


    private fun initKoin() {
        startKoin {
            androidContext(this@MainApplication)
                modules(appModule, dataModule)
        }
    }

    private fun changeStatusBarColor(activity: Activity) {
        activity.window.statusBarColor = activity.resources.getColor(R.color.purple_dark)
    }
}