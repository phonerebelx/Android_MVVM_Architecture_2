package com.example.architecture.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log


/**
 * @author Abdullah Nagori
 */


class InternetHelper (private val context: Context) {

    fun isNetworkAvailable(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true)) {
                if (ni.isConnected) {
                    haveConnectedWifi = true
                    Log.v("WIFI CONNECTION ", "AVAILABLE")
                } else {
                    Log.v("WIFI CONNECTION ", "NOT AVAILABLE")
                }
            }
            if (ni.typeName.equals("MOBILE", ignoreCase = true)) {
                if (ni.isConnected) {
                    haveConnectedMobile = true
                    Log.v("INTERNET CONNECTION ", "AVAILABLE")
                } else {
                    Log.v("INTERNET CONNECTION ", "NOT AVAILABLE")
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile
    }
}