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
import com.example.meezan360.databinding.FragmentResetPasswordBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.interfaces.ApiListener
import com.example.meezan360.model.changePassword.VerifyPassModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.ChangePasswordActivity.ChangePasswordActivity
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordFragment : BaseDockFragment(),ApiListener {

    private lateinit var binding: FragmentResetPasswordBinding
    private val myViewModel: LoginViewModel by viewModel()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myViewModel.apiListener = this@ResetPasswordFragment
        initView()
        handleAPIResponse()
        return binding.root
    }

    private fun initView() {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        val sharedPreferences = requireActivity().getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        setOnClickListener()
    }


    private fun setOnClickListener() {
        binding.let {
            it.updatePassword.setOnClickListener {
                when {
                    binding.cpEtNewPass.text?.isEmpty() == true ||
                            binding.cpEtConfirmPass.text?.isEmpty() == true -> {
                        myDockActivity?.showErrorMessage("Please fill all fields!")
                    }
                    binding.cpEtNewPass.text.toString() != binding.cpEtConfirmPass.text.toString() -> {
                        myDockActivity?.showErrorMessage("Passwords do not match! Please make sure you enter the correct password")
                    }
                    else -> {
                        try {
                            verifyPwdReq(
                                Utils.encryptPass(
                                    "23423532",
                                    "1234567891011121",
                                    binding.cpEtNewPass.text.toString()
                                ).toString()
                            )
                        } catch (e: Exception) {
                            Log.e("onBackPressed", e.message.toString())
                        }
                    }
                }

            }

            it.pressBack.setOnClickListener {
                myDockActivity?.popFragment()
            }
        }
    }

    private fun handleAPIResponse() {
        myDockActivity?.hideProgressIndicator()

        lifecycleScope.launch {
            myViewModel.verifyPasswordData.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as DockActivity).handleErrorResponse(it)
//                        myDockActivity?.showErrorMessage(it.data!!.message)
                    }

                    is ResponseModel.Idle -> {

                    }


                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {

                        if (it.data!= null){

                            myDockActivity?.showSuccessMessage(it.data.message)
                            sharedPreferencesManager.clearSharedPreferences()
                            LoginScreen.navController.navigate(R.id.action_resetPasswordFragment_to_welcome_login)
                        }

//                        if (it.data?.body() != null && it.data.body()?.token!!.isNotEmpty()) {
//                            sharedPreferencesManager.saveToken(it.data.body()?.token.toString())
//                            val bundle = Bundle()
//                            bundle.putString("LOGIN_ID", loginID)
//                            LoginScreen.navController.navigate(R.id.action_OTPFragment_to_resetPasswordFragment, bundle)



                    }
                }

            }
        }

    }


    fun verifyPwdReq(pass: String){
        myDockActivity?.showProgressIndicator()
        val verifyPassModel = VerifyPassModel(pass)
        myViewModel.viewModelScope.launch {
            myViewModel.resetPasswordVerify(
                verifyPassModel
            )
        }
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
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun showPasswordChangingInstructions(text: String?) {
        Log.d("showPassword", "showPasswordChangingInstructions: ")
    }




}