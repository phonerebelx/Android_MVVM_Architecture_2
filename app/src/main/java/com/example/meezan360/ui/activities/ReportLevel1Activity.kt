package com.example.meezan360.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.meezan360.R
import com.example.meezan360.databinding.ActivityReportLevel1Binding
import com.example.meezan360.databinding.FragmentDepositTrendBinding

class ReportLevel1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityReportLevel1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLevel1Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}