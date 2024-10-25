package com.example.meezan360.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings.Secure
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.meezan360.BuildConfig
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivityLoginScreenBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginScreen : DockActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    companion object {
        lateinit var appBarConfiguration: AppBarConfiguration
        @SuppressLint("StaticFieldLeak")
        lateinit var navController : NavController

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_login_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.login_start
            )
        )


    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_login_fragment)
        return super.onSupportNavigateUp()
    }

}