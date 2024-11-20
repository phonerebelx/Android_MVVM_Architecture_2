package com.example.meezan360.ui.fragments.LoginNavFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentChangePasswordBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.interfaces.ApiListener
import com.example.meezan360.model.changenewpassword.ChangePasswordModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.activities.LoginScreen

import com.example.meezan360.utils.InternetHelper
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordFragment : BaseDockFragment(), ApiListener {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var login_id: String
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val myViewModel: LoginViewModel by viewModel()
    private lateinit var internetHelper: InternetHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myViewModel.apiListener = this@ChangePasswordFragment
        internetHelper = InternetHelper(requireContext())
        val sharedPreferences =   myDockActivity?.getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = sharedPreferences?.let { SharedPreferencesManager(it) }!!
        initView()
        handleAPIResponse()
        return binding.root
    }


    override fun onStarted() {
        Log.d("onStarted", "showPasswordChangingInstructions: ")
    }

    override fun onSuccess(liveData: LiveData<String>, tag: String) {
        Log.d("onSuccess", "showPasswordChangingInstructions: ")
    }

    override fun onFailure(message: String, tag: String) {
        myDockActivity?.hideProgressIndicator()
        when (tag) {
            "Verify_Password_Data" -> {
                myDockActivity?.showErrorMessage(message)
            }
        }
    }

    override fun onFailureWithResponseCode(code: Int, message: String, tag: String) {
        myDockActivity?.hideProgressIndicator()
        when (tag){
            "onFailureWithResponseCode_551" -> {
                sharedPreferencesManager.clearSharedPreferences()
                val intent = Intent(requireContext(), LoginScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            "onFailureWithResponseCode_552" -> {

            }
        }
    }

    override fun showPasswordChangingInstructions(text: String?) {
        Log.d("showPassword", "showPasswordChangingInstructions: ")
    }


    private fun initView() {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        login_id = sharedPreferencesManager.getLoginId().toString()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.let {

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

                        if(!internetHelper.isNetworkAvailable()){
                            myDockActivity?.showInternetConnectionMessage("Please connect to Wi-Fi or enable mobile data.")
                        } else {
                            myViewModel.viewModelScope.launch {
                                myDockActivity?.showProgressIndicator()
                                val changePasswordModel =  ChangePasswordModel(
                                    login_id = login_id,
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
                                myViewModel.changePassword(
                                    changePasswordModel.login_id,
                                    changePasswordModel.new_password_confirmation,
                                    changePasswordModel.new_password,
                                    changePasswordModel.old_password
                                )
                            }

                        }
                     }
                }
            }
            myDockActivity?.setPassViewListener(
                binding.cpEtNewPass, binding.cpEtConfirmPass, binding.bothPasswordsMustMatch
            )

        }
    }


    private fun handleAPIResponse() {

        lifecycleScope.launch {
            myDockActivity?.hideProgressIndicator()
            myViewModel.changePasswordpData.collect {

                when (it) {
                    is ResponseModel.Error -> { (requireActivity() as DockActivity).handleErrorResponse(myDockActivity!!,it) }

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        myDockActivity?.showSuccessMessage("Password Change Successfully")
                        sharedPreferencesManager.clearSharedPreferences()
                        startActivity(Intent(requireContext(), LoginScreen::class.java))
                        requireActivity().finish()
                        NavOptions.Builder().setPopUpTo(R.id.login_start, true).build()
                    }
                }

            }
        }

    }
}