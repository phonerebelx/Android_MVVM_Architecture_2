package com.example.meezan360.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.DepositFooterAdapter
import com.example.meezan360.adapter.ReportParentAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.databinding.ActivityReportLevel1Binding
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.reports.FooterBoxes
import com.example.meezan360.model.reports.Report
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.CardLevel.CardLevelActivity
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReportLevel1Activity : DockActivity(),OnTypeItemClickListener {
    private lateinit var binding: ActivityReportLevel1Binding
    private lateinit var topBoxesAdapter: TopBoxesAdapter
    private lateinit var reportParentAdapter: ReportParentAdapter
    private lateinit var footerAdapter: DepositFooterAdapter
    private val myViewModel: ReportViewModel by viewModel()
    private val deposit: String= "Deposit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLevel1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMainFrag.toolbar)
        supportActionBar?.title = ""
        binding.tbMainFrag.toolbarTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.purple_dark
            )
        )

        binding.tbMainFrag.toolbarTitle.text = deposit
        myViewModel.viewModelScope.launch {
            showProgressIndicator()
            myViewModel.getDepositDetails()
        }

        handleAPIResponse()



        binding.tbMainFrag.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

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
                hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        handleErrorResponse(it)

                    }

                    is ResponseModel.Idle -> {}

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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupReportsRecyclerView(reportList: ArrayList<Report>?) {

        binding.recyclerViewReport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reportParentAdapter = ReportParentAdapter(this, reportList, kpiId = "1",deposit)
        reportParentAdapter.getScreenSize = getScreenHeight("width")
        binding.recyclerViewReport.adapter = reportParentAdapter
    }

    private fun setupFooterRecyclerView(footerBoxesList: ArrayList<FooterBoxes>?) {
        binding.recyclerViewFooter.layoutManager = GridLayoutManager(this, 2)
        (binding.recyclerViewFooter.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (position) {
                    0 -> return 2
                    else -> return 1
                }
            }
        }

        footerAdapter = DepositFooterAdapter(this, footerBoxesList,this)
        binding.recyclerViewFooter.adapter = footerAdapter
    }

    override fun <T> onClick(type: String, item: T, position: Int, checked: Boolean?) {
        when (type) {
            "On_Deposit_Footer" -> {
                val getItem = item as FooterBoxes
                val intent = Intent(this, ReportLevel2Activity::class.java)
                intent.putExtra("kpiId", "1")
                intent.putExtra("kpiName", deposit)
                intent.putExtra("tableId", getItem.tableId.toString())
                intent.putExtra("isSubValue", getItem.isSubValue.toString())
                intent.putExtra(
                    "identifierType",
                    ""
                )
                intent.putExtra("identifier", "")
                this.startActivity(intent)
            }
        }


    }

}