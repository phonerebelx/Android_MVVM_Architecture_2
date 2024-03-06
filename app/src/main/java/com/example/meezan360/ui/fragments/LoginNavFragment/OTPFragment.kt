package com.example.meezan360.ui.fragments.LoginNavFragment

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
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OTPFragment : BaseDockFragment() {

    private lateinit var binding: FragmentOTPBinding
    lateinit var loginID: String
    private var isPinnFilled = false
    private lateinit var pin: String
    var isResetPassword = false
    private val myViewModel: LoginViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initView()
        loginID = arguments?.getString("LOGIN_ID", "").toString()
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
                if (editable.toString().length == 6) {
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
//                myDockActivity?.popFragment()
            }
        }
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.verifyOtpData.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
//                        Log.d("ResponseModel.Error: ",it.data?.errorBody()?.string().toString())

                    }

                    is ResponseModel.Idle -> {

                    }


                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        Log.d( "handleAPIResponse: ",it.data?.body().toString())
//                        LoginScreen.navController.navigate(R.id.action_forgetPasswordFragment_to_OTPFragment)

                    }
                }

            }
        }

    }


}