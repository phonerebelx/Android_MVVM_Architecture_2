package com.example.meezan360.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.ReportParentAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.adapter.levelTwoAdapter.TopMenuAdapter
import com.example.meezan360.databinding.ActivityReportLevel2Binding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.reports.Level2ReportModel
import com.example.meezan360.model.reports.Report
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportLevel2Activity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityReportLevel2Binding
    private val myViewModel: ReportViewModel by viewModel()
    private lateinit var topBoxesAdapter: TopBoxesAdapter
    private lateinit var reportParentAdapter: ReportParentAdapter
    private lateinit var topMenuAdapter: TopMenuAdapter
    private var responseBody: ArrayList<Level2ReportModel>? = arrayListOf()
    var kpiId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLevel2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            kpiId = extras.getString("kpiId")
        }

        myViewModel.viewModelScope.launch {
            kpiId?.let { myViewModel.getLevelTwo(it, "0") }
        }
        handleAPIResponse()
    }

    private fun setupTopMenu(topBoxes: ArrayList<String>) {

        binding.recyclerViewTopCategory.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        topMenuAdapter = TopMenuAdapter(this, topBoxes, this)
        binding.recyclerViewTopCategory.adapter = topMenuAdapter
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

                        val topMenuList = ArrayList<String>()

                        responseBody = it.data?.body()

                        if (responseBody != null) {
                            for (i in responseBody!!) {
                                i.topMenu?.let { it1 -> topMenuList.add(it1) }
                            }
                        }
                        setupTopMenu(topMenuList)
                        setupTopBoxes(responseBody?.get(0)?.boxes)
                        setupReportsRecyclerView(responseBody?.get(0)?.table)
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

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        setupTopBoxes(responseBody?.get(position)?.boxes)
        setupReportsRecyclerView(responseBody?.get(position)?.table)
    }
}