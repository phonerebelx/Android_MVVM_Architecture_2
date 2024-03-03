package com.example.meezan360.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.levelTwoAdapter.StepProgressBarAdapter
import com.example.meezan360.databinding.FragmentStepProgressBarBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.google.gson.Gson
import com.loukwn.stagestepbar.StageStepBar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class StepProgressBarFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment() {

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
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                        Toast.makeText(
                            context,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {

                        val responseBody = it.data?.body()
                        val graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()

                        if (responseBody != null) {
                            if (responseBody.isJsonArray) {
                                responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                                    val jsonArray = responseBody.asJsonArray.get(index).toString()
                                    Log.d("jsonArray", jsonArray.toString())
                                    graphModel.add(
                                        Gson().fromJson(
                                            jsonArray,
                                            HorizontalGraphModel::class.java
                                        )
                                    )
                                }


                                if (graphModel.isNotEmpty()) setupRecyclerView(graphModel[0].barChartModel)
                            }
                        }


                    }
                }
            }
        }

    }


}