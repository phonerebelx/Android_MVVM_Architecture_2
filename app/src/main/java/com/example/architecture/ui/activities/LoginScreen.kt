package com.example.architecture.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.architecture.R
import com.example.architecture.databinding.ActivityLoginScreenBinding
class LoginScreen : DockActivity() {

    private lateinit var binding: ActivityLoginScreenBinding

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavController
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up custom toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_login_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up AppBarConfiguration - Set only login_start as top level
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.login_start))

        // Setup the ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configure back navigation
        binding.toolbar.setNavigationOnClickListener {
            onSupportNavigateUp()
        }

        // Update toolbar title and back button based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Set title
            val title = when (destination.id) {
                R.id.login_start -> ""
                R.id.forgetPasswordFragment -> "Forget Password"
                R.id.OTPFragment -> "OTP Verification"
                R.id.changePasswordFragment -> "Change Password"
                R.id.resetPasswordFragment -> "Reset Password"
                else -> ""
            }
            binding.toolbarTitle.text = title


            if (destination.id == R.id.login_start) {
                binding.toolbar.navigationIcon = null
                binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            } else {
                binding.toolbar.setBackgroundColor(resources.getColor(R.color.purple_light, theme))

                binding.toolbar.setNavigationIcon(R.drawable.ic_back)
            }

            binding.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}