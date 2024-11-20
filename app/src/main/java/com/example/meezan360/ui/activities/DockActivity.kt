package com.example.meezan360.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.meezan360.R
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.progress.ProgressDialog
import com.uhfsolutions.carlutions.progress.ProgressIndicator
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


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

    open fun showErrorMessage(message: String) {

        MotionToast.createColorToast(this,
            "Failed",
            message,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_TOP,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.montserrat_medium))

    }
    open fun showInternetConnectionMessage(message: String) {

        MotionToast.createColorToast(this,
            "Internet connection unavailable.",
            message,
            MotionToastStyle.NO_INTERNET,
            MotionToast.GRAVITY_TOP,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.montserrat_medium))

    }
    open fun showSuccessMessage(message: String) {
        MotionToast.createColorToast(this,
            "success",
            message,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_TOP,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.montserrat_medium))

    }
    fun setPassViewListener(password: EditText, confirmPassword: EditText, warningText: TextView) {
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkPasswords(password, confirmPassword, warningText)
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkPasswords(password, confirmPassword, warningText)
            }
            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun checkPasswords(password: EditText, confirmPassword: EditText, warningText: TextView) {
        if (password.text.toString() != confirmPassword.text.toString()) {
            warningText.visibility = View.VISIBLE
        } else {
            warningText.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getScreenHeight(getParameter: String): String{
        val windowMetrics = this.windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val density = resources.displayMetrics.density
        val widthDp = (bounds.width() / density).toInt()
        val heightDp = (bounds.height() / density).toInt()
        when(getParameter){
            "width" -> {
                return widthDp.toString()
            }
            "height" -> {
                return heightDp.toString()
            }
        }
        return ""
    }
}