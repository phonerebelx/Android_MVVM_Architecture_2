package com.example.meezan360.ui.fragments.LoginNavFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.app.adcarchitecture.model.resetPassword.ResetPasswordModel
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentForgetPasswordBinding
import com.example.meezan360.databinding.FragmentLoginBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.MainActivity
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgetPasswordFragment : BaseDockFragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private val myViewModel: LoginViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferencesManager by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)
        handleAPIResponse()
        setOnClickListener()
        return binding.root
    }

    private fun apiCall(type: String) {
        when (type) {
            "Calling_Reset_Password" -> {
                myDockActivity?.showProgressIndicator()
                val setResetPassModel = ResetPasswordModel(
                    binding.reqOtpEtEmail.text.toString()
                )
                myViewModel.viewModelScope.launch {
                    myViewModel.resetPasswordRequest(
                        setResetPassModel
                    )
                }
            }
        }
    }

    private fun setOnClickListener() {
        binding.run {
            reqOTP.setOnClickListener {
                if (binding.reqOtpEtEmail.text.toString().isNotEmpty()) {
                    apiCall("Calling_Reset_Password")
                }
            }

            pressBack.setOnClickListener {
                LoginScreen.navController.popBackStack()
//                myDockActivity?.popFragment()
            }
        }
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.resetPassData.collect {
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

                        val bundle = Bundle()
                        bundle.putString("LOGIN_ID", binding.reqOtpEtEmail.text.toString())
                        bundle.putBoolean("RESET_PASSWORD", true)

                        LoginScreen.navController.navigate(R.id.action_forgetPasswordFragment_to_OTPFragment,bundle)

                    }
                }

            }
        }

    }


}