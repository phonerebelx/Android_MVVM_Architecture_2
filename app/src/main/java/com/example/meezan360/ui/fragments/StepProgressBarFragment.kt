package com.example.meezan360.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentStepProgressBarBinding


class StepProgressBarFragment : Fragment() {

    private lateinit var binding: FragmentStepProgressBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepProgressBarBinding.inflate(layoutInflater)

        return binding.root
    }

}