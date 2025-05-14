package com.example.architecture.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieCompositionFactory
import com.example.architecture.R
import com.example.architecture.databinding.ActivitySplashBinding
import com.example.architecture.datamodule.local.SharedPreferencesManager
import com.example.architecture.security.EncryptionKeyStoreImpl
import com.example.architecture.utils.Constants
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val sharedPreferenceManager: SharedPreferencesManager by inject()
    val encryptionKeyStore = EncryptionKeyStoreImpl.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val isFirstRun = sharedPreferenceManager.get(Constants.FIRST_TIME) as Boolean?
        encryptionKeyStore.setContext(this)

        if (isFirstRun == null) {
            sharedPreferenceManager.put(false, Constants.FIRST_TIME)
            encryptionKeyStore.generateKey()
        } else {
            encryptionKeyStore.loadKey()
        }

        // Initialize Lottie animation
        LottieCompositionFactory.fromRawRes(this, R.raw.meezan).addListener { composition ->
            binding.lottieAnimationView.setComposition(composition)
            binding.lottieAnimationView.playAnimation()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000)
    }

    private fun navigateToNextScreen() {
        val nextActivity = if (sharedPreferenceManager.getToken() != null) {
            MainActivity::class.java
        } else {
            LoginScreen::class.java
        }
        val intent = Intent(this, nextActivity)
        startActivity(intent)
        finish()
    }
}
