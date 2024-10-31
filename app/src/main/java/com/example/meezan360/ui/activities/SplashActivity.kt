package com.example.meezan360.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieCompositionFactory
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivitySplashBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val sharedPreferenceManager: SharedPreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
