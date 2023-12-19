package com.example.meezan360.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings.Secure
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivityLoginScreenBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.viewmodel.MyViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginScreen : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginScreenBinding
    private val myViewModel: MyViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)

        handleAPIResponse()

        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnLogin -> {

                val email = binding.etEmail.text.toString() //waqas
                val password = Utils.encryptPass(
                    "23423532",
                    "1234567891011121",
                    binding.etPassword.text.toString()
                )
                val deviceId = Secure.getString(
                    applicationContext.contentResolver,
                    Secure.ANDROID_ID
                )

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(applicationContext, "Please Enter email", Toast.LENGTH_SHORT)
                        .show()
                    return
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(applicationContext, "Please Enter password", Toast.LENGTH_SHORT)
                        .show()
                    return
                }

                myViewModel.viewModelScope.launch {
                    myViewModel.loginRequest(
                        email,
                        password!!,
                        deviceId
                    )
                }

            }
        }
    }

    private fun handleAPIResponse() {
        lifecycleScope.launchWhenStarted {
            myViewModel.loginData.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            applicationContext,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {

                    }
//                        Toast.makeText(
//                        applicationContext,
//                        "Idle: " + it.message,
//                        Toast.LENGTH_SHORT
//                    ).show()

                    is ResponseModel.Loading -> Toast.makeText(
                        applicationContext,
                        "Loading: " + it.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is ResponseModel.Success -> {

                        sharedPreferenceManager.saveToken(it.data?.body()?.token)

                        val intent = Intent(this@LoginScreen, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }

    }
}