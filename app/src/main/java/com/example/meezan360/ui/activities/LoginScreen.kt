package com.example.meezan360.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivityLoginScreenBinding
import com.example.meezan360.datamodule.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.viewmodel.MyViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginScreen : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginScreenBinding
    private val myViewModel: MyViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT;
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)

        response()

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
                val password = Utils.md5(binding.etPassword.text.toString()) //3zH+fS2agritJwMfv/7wEQ==:MTIzNDU2Nzg5MTAxMTEyMQ==
                val deviceId = Secure.getString(
                    applicationContext.contentResolver,
                    Secure.ANDROID_ID
                ) //321321

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
                        password,
                        deviceId
                    )
                }

            }
        }
    }

    private fun response() {
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
                        val intent = Intent(this@LoginScreen, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }

    }
}