package com.example.meezan360.ui.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivityLoginScreenBinding
import com.example.meezan360.datamodule.di.NetworkModule
import com.example.meezan360.datamodule.di.dataModule
import com.example.meezan360.datamodule.network.ResponseModel
import com.example.meezan360.datamodule.repository.DataRepository
import com.example.meezan360.viewmodel.MyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.platform.android.BouncyCastleSocketAdapter.Companion.factory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class LoginScreen : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT;

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
//        DataRepository(NetworkModule(applicationContext))
//        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
//        response()

        setContentView(binding.root)

        initViews()
    }


    private fun initViews() {
        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnLogin -> {

//                viewModel.viewModelScope.launch {
//                    viewModel.loginRequest("waqas", "12345678", "321321")
//                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun response() {

        viewModel.viewModelScope.launch {
            viewModel.loginData.collectLatest { it ->

                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            applicationContext,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> Toast.makeText(
                        applicationContext,
                        "Idle: " + it.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is ResponseModel.Loading -> Toast.makeText(
                        applicationContext,
                        "Loading: " + it.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is ResponseModel.Success -> Toast.makeText(
                        applicationContext,
                        "Success: " + it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}