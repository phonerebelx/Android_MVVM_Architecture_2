package com.example.meezan360.ui.fragments.LoginNavFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.BuildConfig
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.ActivityLoginScreenBinding
import com.example.meezan360.databinding.FragmentLoginBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.MainActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseDockFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val myViewModel: LoginViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferencesManager by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        handleAPIResponse()

        initViews()
        return binding.root
    }

    private fun initViews() {
        setOnCLickListener()
    }


    @SuppressLint("HardwareIds")
    private fun setOnCLickListener(){
        binding.btnLogin.setOnClickListener {

            var email = binding.etEmail.text.toString() //waqas
            var password = Utils.encryptPass(
                "23423532",
                "1234567891011121",
                binding.etPassword.text.toString()
            )
            val deviceId = Settings.Secure.getString(
                requireContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )

            if (BuildConfig.DEBUG) {
                email = "waqas"
                password = Utils.encryptPass(
                    "23423532",
                    "1234567891011121", "Uhf@1234"
                )
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Please Enter email", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Please Enter password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            myViewModel.viewModelScope.launch {
                myDockActivity?.showProgressIndicator()
                myViewModel.loginRequest(
                    email,
                    password!!,
                    deviceId
                )
            }
        }

        binding.tvForgetPassword.setOnClickListener {
            LoginScreen.navController.navigate(R.id.action_nav_login_fragment_to_nav_forget_fragment)
        }

    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myDockActivity?.hideProgressIndicator()
            myViewModel.loginData.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {

                    }


                    is ResponseModel.Loading ->{}
//                        Toast.makeText(
//                            applicationContext,
//                            "Loading.. ",
//                            Toast.LENGTH_SHORT
//                        ).show()

                    is ResponseModel.Success -> {
                        sharedPreferenceManager.saveToken(it.data?.body()?.token)
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish();
                    }
                }

            }
        }

    }
}