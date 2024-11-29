package com.example.meezan360.ui.fragments.LoginNavFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.BuildConfig
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentLoginBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.MainActivity
import com.example.meezan360.utils.Constants
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.uhfsolutions.sfts.model.fingerprintLoginData.FingerprintLoginData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class LoginFragment : BaseDockFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val myViewModel: LoginViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferencesManager by inject()
    lateinit var montserratFont: Typeface
    lateinit var email: String
    lateinit var password: String
    private var isAuthenticated = false
    private var firstTime = false
    lateinit var executor: Executor
    lateinit var fingerprintData: FingerprintLoginData
    lateinit var bmPrompt: BiometricPrompt
    private var resetPassJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)


        montserratFont =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular) ?: Typeface.DEFAULT

        binding.etPassword.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.etPassword.typeface = Typeface.create(montserratFont, Typeface.BOLD)
        binding.togglePasswordVisibility.setImageResource(R.drawable.eye_closed)

        handleAPIResponse()

        initViews()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        setOnCLickListener()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("HardwareIds")
    private fun setOnCLickListener() {
        binding.togglePasswordVisibility.setOnClickListener {
            if (binding.etPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {

                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_closed)
            } else {

                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_visible)
            }
            binding.etPassword.typeface = Typeface.create(montserratFont, Typeface.BOLD)
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
        montserratFont =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular) ?: Typeface.DEFAULT

        binding.btnLogin.setOnClickListener {
            isAuthenticated = false
            loginUser()
        }

        binding.tvForgetPassword.setOnClickListener {
            LoginScreen.navController.navigate(R.id.action_nav_login_fragment_to_nav_forget_pass_fragment)
        }

        binding.IVfingerpint.setOnClickListener {
            fingerprintPrompt()
        }
    }

    private fun handleAPIResponse() {
        resetPassJob?.cancel()
        resetPassJob = lifecycleScope.launch {
            myDockActivity?.hideProgressIndicator()
            myViewModel.loginData.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        myDockActivity?.hideProgressIndicator()
                        myDockActivity?.handleErrorResponse(myDockActivity!!, it)
                    }

                    is ResponseModel.Idle -> {
                        myDockActivity?.hideProgressIndicator()
                    }


                    is ResponseModel.Loading -> {
                        myDockActivity?.showProgressIndicator()
                    }


                    is ResponseModel.Success -> {
                        if (it.data?.body()?.twoFactor == "yes") {

                            val bundle = Bundle()
                            bundle.putString("LOGIN_ID", binding.etEmail.text.toString())
                            bundle.putString("USER_EMAIL", binding.etEmail.text.toString())
                            bundle.putString("USER_NAME", binding.etEmail.text.toString())
                            bundle.putString("COME_FROM", "COME_FROM_LOGIN_SCREEN")
                            bundle.putBoolean("RESET_PASSWORD", false)

                            SmsRetriever.getClient(requireContext()).startSmsUserConsent(null)
                                .addOnSuccessListener {
                                   myDockActivity?.showSuccessMessage("Started SMS user consent")

                                }.addOnFailureListener {
                                    myDockActivity?.showSuccessMessage("Failed to start SMS user consent")
                                }

                            LoginScreen.navController.navigate(
                                R.id.action_nav_login_fragment_to_OTP_Fragment,
                                bundle
                            )

                        } else {
                            sharedPreferenceManager.saveToken(it.data?.body()?.token)
                            sharedPreferenceManager.saveLoginId(it.data?.body()?.user?.fullName)
                            sharedPreferenceManager.saveUserEmail(it.data?.body()?.user?.emailAddress)
                            sharedPreferenceManager.saveUserName(it.data?.body()?.user?.fullName)
                            sharedPreferenceManager.saveUserId(it.data?.body()?.user?.userId)
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish();
                        }

                        // check for fingerprint flow. that's why its
//                        try {
//                            if (!isAuthenticated) {
//                                val fingerprintData: FingerprintLoginData =
//                                    sharedPreferenceManager.getFingerprintLoginData()
//                                val checkEmail = fingerprintData.user_id
//                                if (email != checkEmail) {
//                                    sharedPreferenceManager.put(false, Constants.IS_FINGERPRINT)
//                                }
//                            }
//                        } catch (E: Exception) {
//                        }
//                        // AMMAR - Saves email and password in sharedPrefManager
//                        sharedPreferenceManager.setFingerprintLoginData(
//                            FingerprintLoginData(
//                                email,
//                                password
//                            )
//                        )

                    }
                }

            }
        }

    }



    @SuppressLint("NewApi")
    private fun checkForFingerPrint() {

        executor = Executors.newSingleThreadExecutor()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            requireContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
            requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_FACE)
        ) {
            bmPrompt = BiometricPrompt.Builder(requireContext())
                .setTitle(Utils.changeDescriptionColor(requireContext(),"Login"))
                .setDescription(Utils.changeDescriptionColor(requireContext(),"Please scan your fingerprint"))
                .setNegativeButton("Cancel", executor,
                    { dialogInterface, i -> isAuthenticated = false }).build()
        }

        if (bmPrompt != null) {
            bmPrompt.authenticate(
                CancellationSignal(),
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        myDockActivity?.runOnUiThread {
//                            Log.i("xxResult_Failed", "Authentication Failed!")
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        myDockActivity?.runOnUiThread {
//                            Log.i("xxResult_Success", result.toString())
                            isAuthenticated = true
                            loginUser()
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
//                        Log.d(TAG, "onAuthenticationError -> $errorCode :: $errString")
                        if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED) {
                            isAuthenticated = false
                        }
                    }
                })
        }
    }

    private fun loginUser() {

        email = binding.etEmail.text.toString() //waqas
        password = binding.etPassword.text.toString()
        val deviceId = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        if (BuildConfig.DEBUG) {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()
        }

        if (!isAuthenticated && TextUtils.isEmpty(email)) {
            myDockActivity?.showErrorMessage("Please Enter email")
            return
        }

        if (!isAuthenticated && TextUtils.isEmpty(password)) {
            myDockActivity?.showErrorMessage("Please Enter password")
            return
        }
        if (sharedPreferenceManager.get<Boolean>(Constants.IS_FINGERPRINT) != null &&
            sharedPreferenceManager.get<Boolean>(Constants.IS_FINGERPRINT)!! && isAuthenticated
        ) {

            val deviceId = Utils.getDeviceId(requireContext())
            val fingerprintKey = Utils.getEncryptedKey(requireContext()).toString()
            myViewModel.viewModelScope.launch {
                myViewModel.loginFingerPrint(fingerprintKey,deviceId)
            }
        }

        else {
            myViewModel.viewModelScope.launch {
                myViewModel.loginRequest(
                    email,
//                Utils.encryptPass(
//                    "23423532",
//                    "1234567891011121",
//                    password
//                )!!
                    Utils.encryptPassNew(password)
                    , deviceId
                )
            }
        }



    }


    private fun fingerprintPrompt() {
        try {
            if (sharedPreferenceManager.get<Boolean>(Constants.IS_FINGERPRINT)!!) {
                checkForFingerPrint()
            } else if (firstTime) {
                myDockActivity?.showErrorMessage("Fingerprint is not enabled!")
            } else {
                myDockActivity?.showErrorMessage("Fingerprint is not enabled!")

                firstTime = true
            }
        } catch (E: Exception) {
            myDockActivity?.showErrorMessage("Fingerprint is not enabled!")
            sharedPreferenceManager.put(false, Constants.IS_FINGERPRINT)
            firstTime = true
        }
    }

    override fun onResume() {
        super.onResume()
        handleAPIResponse()
    }
    override fun onStop() {
        super.onStop()
        resetPassJob?.cancel()
        myViewModel.loginData.value = ResponseModel.Idle("Idle State")

    }
}