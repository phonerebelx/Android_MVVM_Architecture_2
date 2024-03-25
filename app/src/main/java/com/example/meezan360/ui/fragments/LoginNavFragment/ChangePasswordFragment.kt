package com.example.meezan360.ui.fragments.LoginNavFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentChangePasswordBinding
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.LoginScreen.Companion.navController
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordFragment : BaseDockFragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var login_id: String
    private val myViewModel: LoginViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initView()
        handleAPIResponse()
        return binding.root
    }

    private fun initView() {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        login_id = arguments?.getString("LOGIN_ID", "").toString()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.let {
//            it.reqOTP.setOnClickListener {
//                LoginActivity.navController.navigate(R.id.action_forgetPasswordFragment_to_OTPFragment)
//            }
            it.cpBtnChangePassword.setOnClickListener {
                when {
                    binding.cpEtNewPass.text?.isEmpty() == true || binding.cpEtConfirmPass.text?.isEmpty() == true -> {
                        myDockActivity?.showErrorMessage("Please fill all fields!")
                    }

                    binding.cpEtNewPass.text.toString() != binding.cpEtConfirmPass.text.toString() -> {
                        myDockActivity?.showErrorMessage("Passwords do not match! Please make sure you enter the correct password")
                    }

                    else -> {
                        // ALI - ProgressIndicator displayed and changePassword request call made
                        myDockActivity?.hideKeyboard(requireView())
                        myDockActivity?.showProgressIndicator()
                        val decodedEmail = String(
                            android.util.Base64.decode(
                                login_id,
                                android.util.Base64.DEFAULT
                            )
                        )

                        myViewModel.viewModelScope.launch {
                            myViewModel.changePassword(
                                ChangePasswordModel(
                                    login_id = decodedEmail,
                                    old_password = Utils.encryptPass(
                                        "23423532",
                                        "1234567891011121",
                                        binding.cpEtCurrentPassword.text.toString()
                                    ).toString(),
                                    new_password = Utils.encryptPass(
                                        "23423532",
                                        "1234567891011121",
                                        binding.cpEtNewPass.text.toString()
                                    ).toString(),
                                    new_password_confirmation = Utils.encryptPass(
                                        "23423532",
                                        "1234567891011121",
                                        binding.cpEtConfirmPass.text.toString()
                                    ).toString()
                                )
                            )

                        }
                    }
                }

            }
            myDockActivity?.setPassViewListener(
                binding.cpEtNewPass, binding.cpEtConfirmPass, binding.bothPasswordsMustMatch
            )
            it.pressBack.setOnClickListener {
                LoginScreen.navController.popBackStack()
            }
        }
    }


    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.changePasswordpData.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                    }

                    is ResponseModel.Idle -> {

                    }


                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        startActivity(Intent(requireContext(), LoginScreen::class.java))
                        requireActivity().finish()
                        NavOptions.Builder().setPopUpTo(R.id.login_start, true).build()


                    }
                }

            }
        }

    }
}