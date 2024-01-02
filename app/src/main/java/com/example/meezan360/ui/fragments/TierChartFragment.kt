package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.FragmentTierWiseDepositBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.TierGraphModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class TierChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(),
    OnItemClickListener {

    private lateinit var binding: FragmentTierWiseDepositBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private val graphModel: ArrayList<TierGraphModel> = arrayListOf()
    private lateinit var adapter: BarChartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTierWiseDepositBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()

        return binding.root
    }

    private fun setupPyramidChart(horizontalGraphModel: TierGraphModel) {

        val pyramidChart = AnyChart.pyramid()

        val colors = ArrayList<Int>()
        val mData = ArrayList<ValueDataEntry>()

        horizontalGraphModel.data.forEachIndexed { index, tierChartDataModel ->
            mData.add(ValueDataEntry(tierChartDataModel.key, tierChartDataModel.value))
            colors.add(Color.parseColor(tierChartDataModel.color))
        }
        pyramidChart.apply {
            data(mData as List<DataEntry>?)
//            fill(colors)
//            fill("aquastyle")
            legend().enabled(true)
            legend().fontSize(12)
            legend().padding(0, 0, 10, 0)
//            title().padding(10, 0, 10, 0)
//            title().fontSize(18)
//            title().fontColor("#333")
            labels().format("{%Value}%")
            labels().fontColor("#ffffff")
            labels().fontSize(10)
            labels().position("inside")
            credits(false)
        }

        // Display the chart
        binding.anyChartView.setChart(pyramidChart)
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.footerGraph.collect {
                when (it) {
                    is ResponseModel.Error -> {
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
                        val recyclerViewItems: ArrayList<String> = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel.add(
                                Gson().fromJson(
                                    jsonArray,
                                    TierGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it1 -> recyclerViewItems.add(it1) }
                        }

                        setupRecyclerView(recyclerViewItems)

                        setupPyramidChart(graphModel[0])

                    }
                }
            }
        }

    }

    private fun setupRecyclerView(listItems: ArrayList<String>) {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        adapter = BarChartAdapter(requireContext(), listItems, this)
        binding.recyclerView.adapter = adapter

    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        setupPyramidChart(graphModel[position])

    }

}