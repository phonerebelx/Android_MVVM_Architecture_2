package com.example.meezan360.ui.fragments.LoginNavFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
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
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseDockFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val myViewModel: LoginViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferencesManager by inject()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        handleAPIResponse()
        findNavController().popBackStack(R.id.OTPFragment, true)
        initViews()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        setOnCLickListener()
    }


    @RequiresApi(Build.VERSION_CODES.N)
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
                email =  binding.etEmail.text.toString()
                password = Utils.encryptPass(
                    "23423532",
                    "1234567891011121",
                    binding.etPassword.text.toString()
                )
            }

            if (TextUtils.isEmpty(email)) {

                myDockActivity?.showErrorMessage("Please Enter email")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {

                myDockActivity?.showErrorMessage("Please Enter password")
                return@setOnClickListener
            }

            myViewModel.viewModelScope.launch {
                myViewModel.loginRequest(email, password!!, deviceId)

            }
        }

        binding.tvForgetPassword.setOnClickListener {
            LoginScreen.navController.navigate(R.id.action_nav_login_fragment_to_nav_forget_pass_fragment)
        }

    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myDockActivity?.hideProgressIndicator()
            myViewModel.loginData.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        myDockActivity?.hideProgressIndicator()
                        myDockActivity?.handleErrorResponse(it)

                    }

                    is ResponseModel.Idle -> {
                        myDockActivity?.hideProgressIndicator()
                    }


                    is ResponseModel.Loading ->{
                        myDockActivity?.showProgressIndicator()
                    }


                    is ResponseModel.Success -> {
                        if (it.data?.body()?.twoFactor == "yes"){
                            val bundle = Bundle()
                            bundle.putString("LOGIN_ID", binding.etEmail.text.toString())
                            bundle.putString("USER_EMAIL", binding.etEmail.text.toString())
                            bundle.putString("USER_NAME", binding.etEmail.text.toString())
                            bundle.putString("COME_FROM", "COME_FROM_LOGIN_SCREEN")
                            bundle.putBoolean("RESET_PASSWORD", false)
                            LoginScreen.navController.navigate(
                                R.id.action_nav_login_fragment_to_OTP_Fragment,
                                bundle
                            )
                        }else{
                            sharedPreferenceManager.saveToken(it.data?.body()?.token)
                            sharedPreferenceManager.saveLoginId(binding.etEmail.text.toString())
                            sharedPreferenceManager.saveUserEmail(it.data?.body()?.user?.emailAddress)
                            sharedPreferenceManager.saveUserName(it.data?.body()?.user?.fullName)
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish();
                        }

                    }
                }

            }
        }

    }
}