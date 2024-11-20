package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentTierWiseDepositBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.TierGraphModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class TierChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    BaseDockFragment(),
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
            colors.add(Utils.parseColorSafely(tierChartDataModel.color))
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
                        hideProgressIndicator()
                        (requireActivity() as DockActivity).handleErrorResponse(myDockActivity!!,it)

                    }

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {showProgressIndicator()}

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
                        val responseBody = it.data?.body()
                        if (responseBody?.asJsonArray?.isEmpty == true){
                            binding.anyChartView.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }
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

                        if (graphModel.isNotEmpty()) setupPyramidChart(graphModel[0])

                    }
                }
            }
        }

    }

    private fun setupRecyclerView(listItems: ArrayList<String>) {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        adapter = BarChartAdapter(requireContext(), listItems, this)
        binding.recyclerView.adapter = adapter

    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        setupPyramidChart(graphModel[position])

    }

    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.anyChartView.visibility = View.GONE

    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.anyChartView.visibility = View.VISIBLE
    }

}