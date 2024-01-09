package com.example.meezan360.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.DepositFooterAdapter
import com.example.meezan360.adapter.ReportParentAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.databinding.ActivityReportLevel1Binding
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.reports.FooterBoxes
import com.example.meezan360.model.reports.Report
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportLevel1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityReportLevel1Binding
    private lateinit var topBoxesAdapter: TopBoxesAdapter
    private lateinit var reportParentAdapter: ReportParentAdapter
    private lateinit var footerAdapter: DepositFooterAdapter
    private val myViewModel: ReportViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLevel1Binding.inflate(layoutInflater)
        myViewModel.viewModelScope.launch {
            myViewModel.getDepositDetails()
        }
        handleAPIResponse()

        setContentView(binding.root)
    }

    private fun setupTopBoxes(topBoxes: ArrayList<TopBoxesModel>?) {

        binding.recyclerViewTopBox.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        topBoxesAdapter = TopBoxesAdapter(this, topBoxes)
        binding.recyclerViewTopBox.adapter = topBoxesAdapter
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.depositDetail.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            this@ReportLevel1Activity, "error: " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        val responseBody = it.data?.body()

                        setupTopBoxes(responseBody?.topBoxes)
                        setupReportsRecyclerView(responseBody?.report)
                        setupFooterRecyclerView(responseBody?.footerBoxes)
                    }
                }
            }
        }
    }

    private fun setupReportsRecyclerView(reportList: ArrayList<Report>?) {
        binding.recyclerViewReport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reportParentAdapter = ReportParentAdapter(this, reportList)
        binding.recyclerViewReport.adapter = reportParentAdapter
    }

    private fun setupFooterRecyclerView(footerBoxesList: ArrayList<FooterBoxes>?) {
        binding.recyclerViewFooter.layoutManager = GridLayoutManager(this, 2)
        footerAdapter = DepositFooterAdapter(this, footerBoxesList)
        binding.recyclerViewFooter.adapter = footerAdapter
    }

}