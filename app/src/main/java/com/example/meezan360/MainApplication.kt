package com.example.meezan360

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.ThreatListener
import com.example.meezan360.di.appModule
import com.example.meezan360.di.dataModule
import com.example.meezan360.security.EncryptionKeyStoreImpl
import com.example.meezan360.security.logger.SecureLogger
import com.example.meezan360.utils.Constants
import com.example.meezan360.utils.InternetHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class MainApplication : Application(), ThreatListener.ThreatDetected {

    lateinit var internetHelper: InternetHelper
    val encryptionKeyStore = EncryptionKeyStoreImpl.instance

    override fun onCreate() {
        super.onCreate()
        encryptionKeyStore.setContext(this)
        encryptionKeyStore.generateKey()

        SecureLogger.init(this)
        try {
            SecureLogger.processLogcat(this)
        } catch (e: Exception) {
            Log.e("MainApplication", "Error processing logcat", e)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }else {
            Timber.plant(ReleaseTree(applicationContext))
        }
        initTalsec()

        internetHelper = InternetHelper(applicationContext)

        initKoin()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                    changeStatusBarColor(activity)
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

    private fun initTalsec() {
        val config = TalsecConfig(
            applicationContext.packageName,
            arrayOf(
                "8D:86:48:C1:6E:49:6D:4F:C6:77:0A:B4:26:CC:AA:12:A8:4A:DF:76:47:FD:11:CE:77:04:1B:64:6F:49:20:37"
            ),
            "",
            null,
            true
        )

        ThreatListener(this).registerListener(this)
        Talsec.start(this, config)
    }


    private fun changeStatusBarColor(activity: Activity) {
        val colorId = R.color.purple_dark
        try {
            activity.window.statusBarColor = activity.resources.getColor(colorId)
        } catch (e: Resources.NotFoundException) {
            Log.e("MainApplication", "Resource not found: $colorId", e)
            // Optionally, set a default color or handle the error gracefully
            activity.window.statusBarColor = activity.resources.getColor(android.R.color.black)
        }
    }



    override fun onRootDetected() {
//        Toast.makeText(this, "onRootDetected", Toast.LENGTH_LONG).show()
        System.exit(0)
    }

    override fun onDebuggerDetected() {
//        Toast.makeText(this, "onDebuggerDetected", Toast.LENGTH_LONG).show()

//        System.exit(0)
    }

    override fun onEmulatorDetected() {
//        Toast.makeText(this, "onEmulatorDetected", Toast.LENGTH_LONG).show()
//        System.exit(0)
    }

    override fun onTamperDetected() {

    }

    override fun onUntrustedInstallationSourceDetected() {

    }

    override fun onHookDetected() {

//        Toast.makeText(this, "onHookDetected", Toast.LENGTH_LONG).show()
//        System.exit(0)
    }

    override fun onDeviceBindingDetected() {
//        Toast.makeText(this, "onDeviceBindingDetected", Toast.LENGTH_LONG).show()
//        System.exit(0)
    }

    override fun onObfuscationIssuesDetected() {
//        Toast.makeText(this, "onObfuscationIssuesDetected", Toast.LENGTH_LONG).show()
//        System.exit(0)
    }

}



class ReleaseTree(val context: Context) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            Toast.makeText(context, "Log Visible: $message", Toast.LENGTH_LONG).show()
        }
    }
}
