package com.example.meezan360.ui.fragments.LoginNavFragment


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentOTPBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.model.otp.OtpModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.MainActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.regex.Pattern

class OTPFragment : BaseDockFragment() {

    private lateinit var binding: FragmentOTPBinding
    lateinit var loginID: String
    lateinit var userName: String
    private var isPinnFilled = false
    private lateinit var pin: String
    var isResetPassword = false
    private var comeFromType: String? = null
    private var resetPassJob: Job? = null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val myViewModel: LoginViewModel by viewModel()
    private var backPressedTime: Long = 0
    private val backPressInterval = 2000

    private var smsReceiver: BroadcastReceiver? = null

    private var smsConsentRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {

                val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)

                message?.let {

                    val otpCode = Regex("\\b\\d{4}\\b").find(it)?.value

                    if (otpCode != null) {
                        binding.pinView.setText(otpCode)
                    } else {
                        Timber.tag("MyCode").d("No OTP found in the message")
                    }

                    Timber.tag("received").d(it)


                }
            }
        }

    private val smsBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                val extras = intent.extras
                val smsRetrieverStatus =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras?.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
                    }


                when (smsRetrieverStatus?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                extras?.getParcelable(
                                    SmsRetriever.EXTRA_CONSENT_INTENT,
                                    Intent::class.java
                                )
                            } else {
                                extras?.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                            }
                        try {
                            consentIntent?.let {
                                smsConsentRequest.launch(it)
                            }
                        } catch (e: Exception) {
                            myDockActivity?.showErrorMessage("Error in recieving OTP")
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        myDockActivity?.showErrorMessage("Timeout Error")
                    }

                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        initView()

        //otp screen change
        loginID = arguments?.getString("LOGIN_ID", "").toString()
        userName = arguments?.getString("USER_NAME", "").toString()

        comeFromType = arguments?.getString("COME_FROM", "").toString()
        val sharedPreferences =
            requireActivity().getSharedPreferences(
                "Meezan360",
                Context.MODE_PRIVATE
            )
        sharedPreferencesManager =
            SharedPreferencesManager(sharedPreferences)
        binding.loginID.text = loginID
        isResetPassword =
            arguments?.getBoolean("RESET_PASSWORD") == true

        if (comeFromType == "COME_FROM_LOGIN_SCREEN") {
//            binding.tvHeading.text = "OTP"
        }
        handleAPIResponse()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    private fun initView() {
        binding = FragmentOTPBinding.inflate(layoutInflater)
//        (activity as LoginActivity).supportActionBar?.hide()
        setOnClickListener()
        setPinViewListener()
    }

    private fun setPinViewListener() {
        binding.pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

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
                    )
                )
            }

        }
        binding.pinView.text = null
    }

    private fun setOnClickListener() {
        binding.let {

//            it.pressBack.setOnClickListener {
//                LoginScreen.navController.navigate(R.id.action_OTPFragment_to_login_Fragment)
//
//            }

        }
    }

    private fun handleAPIResponse() {
        resetPassJob = lifecycleScope.launch {
            myViewModel.verifyOtpData.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as DockActivity).handleErrorResponse(
                            myDockActivity!!,
                            it
                        )
                    }

                    is ResponseModel.Idle -> {

                    }


                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {

                        if (comeFromType == "COME_FROM_FORGET_PASSWORD") {
                            if (it.data?.body() != null && it.data?.body()?.token!!.isNotEmpty()) {
                                sharedPreferencesManager.saveToken(it.data?.body()?.token.toString())
                                val bundle = Bundle()
                                bundle.putString("LOGIN_ID", loginID)
                                LoginScreen.navController.navigate(
                                    R.id.action_OTPFragment_to_resetPasswordFragment,
                                    bundle
                                )
                            }

                        } else if (comeFromType == "COME_FROM_LOGIN_SCREEN") {
//                            binding.tvHeading.text = "OTP"
                            sharedPreferencesManager.clearSharedPreferences()
                            sharedPreferencesManager.saveToken(it.data?.body()?.token)
                            sharedPreferencesManager.saveUserName(
                                loginID
                            )
                            sharedPreferencesManager.saveUserEmail(
                                userName
                            )
                            sharedPreferencesManager.saveUserId(it.data?.body()?.user_id)
                            val intent = Intent(
                                requireContext(),
                                MainActivity::class.java
                            )
                            startActivity(intent)
                            requireActivity().finish();
                        }

                    }
                }

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the receiver to prevent memory leaks
        smsReceiver?.let {
            try {
                requireContext().unregisterReceiver(it)
            } catch (e: Exception) {
                Timber.e("Error unregistering receiver: ${e.message}")
            }
        }
        smsReceiver = null
    }


    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            smsBroadcastReceiver, // Your BroadcastReceiver
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onStop() {
        super.onStop()
        resetPassJob?.cancel()
        myViewModel.verifyOtpData.value =
            ResponseModel.Idle("Idle State")
    }
}
