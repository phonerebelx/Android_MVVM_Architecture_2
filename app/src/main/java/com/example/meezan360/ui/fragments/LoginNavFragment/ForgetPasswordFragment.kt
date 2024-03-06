package com.example.meezan360.ui.fragments.LoginNavFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentForgetPasswordBinding
import com.example.meezan360.databinding.FragmentLoginBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.viewmodel.LoginViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private val myViewModel: LoginViewModel by viewModel()
    private val sharedPreferenceManager: SharedPreferencesManager by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)

        return binding.root
    }


}