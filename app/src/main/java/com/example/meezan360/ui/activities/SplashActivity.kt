package com.example.meezan360.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.meezan360.databinding.ActivitySplashBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import org.koin.android.ext.android.inject


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    private val sharedPreferenceManager: SharedPreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (sharedPreferenceManager.getToken() != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvSwipe.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()

        }

    }
}