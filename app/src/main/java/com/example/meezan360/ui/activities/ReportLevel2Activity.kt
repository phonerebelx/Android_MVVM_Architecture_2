package com.example.meezan360.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.RatingAdapter.RatingLevelAdapter
import com.example.meezan360.adapter.ReportParentAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.adapter.levelTwoAdapter.TopMenuAdapter
import com.example.meezan360.databinding.ActivityReportLevel2Binding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.reports.Level2ReportModel
import com.example.meezan360.model.reports.RatingModels.Rating
import com.example.meezan360.model.reports.RatingModels.RatingData
import com.example.meezan360.model.reports.RatingModels.RatingDetail
import com.example.meezan360.model.reports.Report
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.log

class ReportLevel2Activity : DockActivity(), OnItemClickListener {
    private lateinit var binding: ActivityReportLevel2Binding
    private val myViewModel: ReportViewModel by viewModel()
    private lateinit var topBoxesAdapter: TopBoxesAdapter
    private lateinit var topRatingAdapter: RatingLevelAdapter
    private lateinit var reportParentAdapter: ReportParentAdapter
    private lateinit var topMenuAdapter: TopMenuAdapter
    private var responseBody: ArrayList<Level2ReportModel>? = arrayListOf()
    private lateinit var reportArray: ArrayList<Report>
    var kpiId: String? = null
    private var tableId: String = "0"
    private var identifierType: String = ""
    private var identifier: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLevel2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMainFrag.toolbar)
        supportActionBar?.title = ""
        binding.tbMainFrag.toolbarTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.purple_dark
            )
        )
        val extras = intent.extras
        if (extras != null) {
            kpiId = extras.getString("kpiId")
            tableId = extras.getString("tableId").toString()
            identifierType = extras.getString("identifierType").toString()
            identifier = extras.getString("identifier").toString()
        }


        myViewModel.viewModelScope.launch {
            showProgressIndicator()
            kpiId?.let { myViewModel.getLevelTwo(it, tableId, identifierType, identifier) }
        }

        handleAPIResponse()

        binding.tbMainFrag.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


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


    private fun setupTopRatingDetail(topRatingDetail: List<RatingDetail>?) {
        binding.tvRatingDetailTitle.text = topRatingDetail?.get(0)?.value.toString() ?: ""
        binding.tvRatingDetailDetail.text = "${topRatingDetail?.get(1)?.key.toString()}: ${topRatingDetail?.get(1)?.value}" ?: ""
    }

    private fun setupTopRating(topRating: ArrayList<RatingData>) {

        binding.recyclerViewRating.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        topRatingAdapter = RatingLevelAdapter(this)
        topRatingAdapter.adapterList = topRating
        binding.recyclerViewRating.adapter = topRatingAdapter
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.levelTwo.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        hideProgressIndicator()
                        handleErrorResponse(it)
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
                        hideProgressIndicator()
                        val topMenuList = ArrayList<String>()

                        responseBody = it.data?.body()

                        if (responseBody != null) {
                            binding.tbMainFrag.toolbarTitle.text = responseBody!!.get(0).table.get(0).table_title


                                for (i in responseBody!!) {
                                i.topMenu?.let { it1 -> topMenuList.add(it1) }
                            }


                            val table = responseBody
                            reportArray = table?.get(0)?.toReport()!!


                            setupTopMenu(topMenuList)

                            if (responseBody?.get(0)?.rating != null) {
                                if (binding.cvRatingDetailBox.isVisible){
                                    binding.cvRatingDetailBox.visibility = View.GONE
                                }
                                binding.cvRatingBox.visibility = View.VISIBLE
                                binding.tvRatingTitle.text =
                                    responseBody?.get(0)?.rating?.rating_title
                                if (responseBody?.get(0)?.rating?.rating_data?.isNotEmpty() == true) {
                                    setupTopRating(responseBody?.get(0)?.rating!!.rating_data)
                                }

                                binding.tvNoOfBranches.text =
                                    responseBody?.get(0)?.rating?.rating_footer

                            }

                            if (responseBody?.get(0)?.rating_detail?.isNotEmpty() == true) {
                                if (binding.cvRatingBox.isVisible){
                                    binding.cvRatingBox.visibility = View.GONE
                                }
                                binding.cvRatingDetailBox.visibility = View.VISIBLE
                                setupTopRatingDetail(responseBody?.get(0)?.rating_detail)

                            }

                            setupTopBoxes(responseBody?.get(0)?.boxes)
                            setupReportsRecyclerView(reportArray)
                        }
                    }
                }
            }
        }
    }

    private fun setupReportsRecyclerView(reportList: ArrayList<Report>?) {
        binding.recyclerViewReport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reportParentAdapter = ReportParentAdapter(this, reportList, kpiId!!)
        binding.recyclerViewReport.adapter = reportParentAdapter
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        val table = responseBody

        reportArray = table?.get(position)?.toReport()!!
        setupTopBoxes(responseBody?.get(position)?.boxes)


        if (responseBody?.get(position)?.rating != null) {

            if (binding.cvRatingDetailBox.isVisible){
                binding.cvRatingDetailBox.visibility = View.GONE
            }
            binding.cvRatingBox.visibility = View.VISIBLE
            binding.tvRatingTitle.text = responseBody?.get(position)?.rating?.rating_title
            if (responseBody?.get(position)?.rating?.rating_data?.isNotEmpty() == true) {
                setupTopRating(responseBody?.get(position)?.rating!!.rating_data)
            } else setupTopRating(arrayListOf())



            binding.tvNoOfBranches.text = responseBody?.get(position)?.rating?.rating_footer
        } else {
            binding.cvRatingBox.visibility = View.GONE
        }

        if (responseBody?.get(position)?.rating_detail != null &&responseBody?.get(position)?.rating_detail?.isNotEmpty() == true) {

            if (binding.cvRatingBox.isVisible) {
                binding.cvRatingBox.visibility = View.GONE
            }

            binding.cvRatingDetailBox.visibility = View.VISIBLE
            setupTopRatingDetail(responseBody?.get(position)?.rating_detail)

        }else{
            binding.cvRatingDetailBox.visibility = View.GONE
        }
        binding.tbMainFrag.toolbarTitle.text = table?.get(position)?.table?.get(0)?.table_title
        setupReportsRecyclerView(reportArray)
    }

}