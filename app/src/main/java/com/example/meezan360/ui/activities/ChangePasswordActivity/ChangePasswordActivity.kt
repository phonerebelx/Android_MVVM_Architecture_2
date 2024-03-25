package com.example.meezan360.ui.activities.ChangePasswordActivity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivityChangePasswordBinding
import com.example.meezan360.databinding.ActivityMainBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.fragments.LoginNavFragment.ChangePasswordFragment

class ChangePasswordActivity : DockActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        if (savedInstanceState == null) {
            val bundle = bundleOf("some_int" to 0)
            bundle.putString("LOGIN_ID",sharedPreferencesManager.getLoginId())
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ChangePasswordFragment>(R.id.fragment_container_view, args = bundle)
            }
        }
    }
}