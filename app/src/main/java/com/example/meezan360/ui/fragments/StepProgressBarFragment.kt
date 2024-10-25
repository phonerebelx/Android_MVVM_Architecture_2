package com.example.meezan360.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.levelTwoAdapter.StepProgressBarAdapter
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentStepProgressBarBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.google.gson.Gson
import com.loukwn.stagestepbar.StageStepBar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class StepProgressBarFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    BaseDockFragment() {

    private lateinit var binding: FragmentStepProgressBarBinding
    private var currentState: StageStepBar.State = StageStepBar.State(2, 0)
    private lateinit var adapter: StepProgressBarAdapter
    private val myViewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepProgressBarBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()

        return binding.root
    }

    private fun setupRecyclerView(graphModel: ArrayList<HorizontalBarChartDataModel>) {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        adapter = StepProgressBarAdapter(requireContext(), graphModel)
        binding.recyclerView.adapter = adapter

    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.footerGraph.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        hideProgressIndicator()
                        (requireActivity() as DockActivity).handleErrorResponse(it)
                    }

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {
                        showProgressIndicator()
                    }

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
                        val responseBody = it.data?.body()
                        if (responseBody?.asJsonArray?.isEmpty == true){
                            binding.recyclerView.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }
                        val graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()

                        if (responseBody != null) {
                            if (responseBody.isJsonArray) {
                                responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                                    val jsonArray = responseBody.asJsonArray.get(index).toString()
                                    graphModel.add(
                                        Gson().fromJson(
                                            jsonArray,
                                            HorizontalGraphModel::class.java
                                        )
                                    )
                                }

                                if (graphModel.isNotEmpty()) binding.tvDescription.text = graphModel[0].description
                                if (graphModel.isNotEmpty()) setupRecyclerView(graphModel[0].barChartModel)
                            }
                        }


                    }
                }
            }
        }

    }
    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.tvDescription.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE



    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.tvDescription.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE


    }

}