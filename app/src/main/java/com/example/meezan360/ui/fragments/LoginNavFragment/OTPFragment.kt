package com.example.meezan360.ui.fragments.LoginNavFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.app.adcarchitecture.model.otp.OtpModel
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentOTPBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.MainActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OTPFragment : BaseDockFragment() {

    private lateinit var binding: FragmentOTPBinding
    lateinit var loginID: String
    private var isPinnFilled = false
    private lateinit var pin: String
    var isResetPassword = false
    private var comeFromType:String? = null
    private var resetPassJob: Job? = null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val myViewModel: LoginViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initView()

            //otp screen change
        loginID = arguments?.getString("LOGIN_ID", "").toString()
        comeFromType = arguments?.getString("COME_FROM", "").toString()
        val sharedPreferences =
            requireActivity().getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        binding.loginID.text = loginID
        isResetPassword = arguments?.getBoolean("RESET_PASSWORD") == true
        handleAPIResponse()
        return binding.root
    }

    private fun initView() {
        binding = FragmentOTPBinding.inflate(layoutInflater)
//        (activity as LoginActivity).supportActionBar?.hide()
        setOnClickListener()
        setPinViewListener()
    }

    private fun setPinViewListener() {
        binding.pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().length == 4) {

                    isPinnFilled = true
                    pin = editable.toString()
                    onVerifyClickEvent()
                    myDockActivity?.hideKeyboard(binding.pinView)
                }
            }
        })
    }

    private fun onVerifyClickEvent() {
        if (isPinnFilled) {
            verifyOTP(pin)
        }
    }

    private fun verifyOTP(otp: String) {
        myDockActivity?.showProgressIndicator()
        if (isResetPassword) {
            myViewModel.viewModelScope.launch {
                myViewModel.verifyOtp(
                    OtpModel(
                        loginID,
                        otp,
                        Utils.getDeviceId(requireContext()),
                        "360",
                        "yes"
                    )
                )
            }
        } else {
            myViewModel.viewModelScope.launch {
                myViewModel.verifyOtp(
                    OtpModel(
                        loginID,
                        otp,
                        Utils.getDeviceId(requireContext()),
                        "360"
                    )
                )
            }

        }
        binding.pinView.text = null
    }

    private fun setOnClickListener() {
        binding.let {

            it.pressBack.setOnClickListener {

                LoginScreen.navController.popBackStack()
            }

        }
    }

    private fun handleAPIResponse() {
        resetPassJob = lifecycleScope.launch {
            myViewModel.verifyOtpData.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                    }

                    is ResponseModel.Idle -> {

                    }


                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {

                        if (comeFromType == "COME_FROM_FORGET_PASSWORD"){
                            if (it.data?.body() != null && it.data.body()?.token!!.isNotEmpty()) {
                                sharedPreferencesManager.saveToken(it.data.body()?.token.toString())
                                val bundle = Bundle()
                                bundle.putString("LOGIN_ID", loginID)
                                LoginScreen.navController.navigate(R.id.action_OTPFragment_to_resetPasswordFragment, bundle)
                            }
                        }else if (comeFromType == "COME_FROM_LOGIN_SCREEN"){
                            sharedPreferencesManager.clearSharedPreferences()
                            sharedPreferencesManager.saveToken(it.data?.body()?.token)
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish();
                        }



                    }
                }

            }
        }

    }


    override fun onStop() {
        super.onStop()
        resetPassJob?.cancel()
        myViewModel.verifyOtpData.value = ResponseModel.Idle("Idle State")
    }
}