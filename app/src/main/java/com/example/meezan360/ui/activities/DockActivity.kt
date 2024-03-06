package com.example.meezan360.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.progress.ProgressDialog
import com.uhfsolutions.carlutions.progress.ProgressIndicator

abstract class DockActivity: AppCompatActivity(), ProgressIndicator {


    private lateinit var progressBarDialog: ProgressDialog

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //      getLocation()
    }


    fun setSharedPref(): SharedPreferencesManager {
        val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        return sharedPreferencesManager
    }

    override fun showProgressIndicator() {
        progressBarDialog = ProgressDialog()
        progressBarDialog.showDialog(
            supportFragmentManager,
            DockActivity::class.java.simpleName
        )
    }

    override fun hideProgressIndicator() {
        if (this::progressBarDialog.isInitialized && progressBarDialog.isAdded) {
            progressBarDialog.dismiss()
        }
    }
    fun popFragment() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    fun hideKeyboard(view: View) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view.applicationWindowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}