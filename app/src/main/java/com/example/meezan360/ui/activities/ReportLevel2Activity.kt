package com.example.meezan360.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.databinding.ActivityReportLevel2Binding
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportLevel2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityReportLevel2Binding
    private val myViewModel: ReportViewModel by viewModel()
    private lateinit var topBoxesAdapter: TopBoxesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLevel2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        myViewModel.viewModelScope.launch {
            myViewModel.getLevelTwo("", "")
        }
        handleAPIResponse()
    }

    private fun setupTopBoxes(topBoxes: ArrayList<TopBoxesModel>?) {

        binding.recyclerViewTopBox.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        topBoxesAdapter = TopBoxesAdapter(this, topBoxes)
        binding.recyclerViewTopBox.adapter = topBoxesAdapter
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.levelTwo.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            this@ReportLevel2Activity,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        val responseBody = it.data?.body()
                        setupTopBoxes(responseBody?.boxes)
//                        setupReportsRecyclerView(responseBody?.report)
                    }
                }
            }
        }
    }
}