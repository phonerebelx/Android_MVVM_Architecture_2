package com.example.meezan360.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.databinding.FragmentDepositCompositionTDBinding


class DepositCompositionTD : Fragment() {

    lateinit var binding: FragmentDepositCompositionTDBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDepositCompositionTDBinding.inflate(layoutInflater)
        return binding.root
    }

}