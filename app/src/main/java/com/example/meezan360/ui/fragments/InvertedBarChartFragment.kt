package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.FragmentCustomerDepositBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvertedBarChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentCustomerDepositBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private val graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()
    private lateinit var adapter: BarChartAdapter
    private var rotationAngle: Float = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentCustomerDepositBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        rotationAngle = if (dataModel.isVerticalLegend == "1")
            -90f
        else
            -0f
        handleAPIResponse()

        return binding.root
    }

//    private fun showBarChart(horizontalGraphModel: HorizontalGraphModel, barChart: BarChart) {
//
//        val valueList = ArrayList<Double>()
//        val entries: ArrayList<BarEntry> = ArrayList()
//        val colors = ArrayList<Int>()
//        val labels = ArrayList<String>()
//
//        horizontalGraphModel.barChartModel.forEachIndexed { index, chartData ->
//            val barEntry = BarEntry(index.toFloat(), chartData.value)
//            entries.add(barEntry)
//            colors.add(Utils.parseColorSafely(chartData.valueColor))
//            labels.add(chartData.key)
//        }
//
//
//        val barDataSet = BarDataSet(entries,"")
//        barDataSet.setDrawValues(true)
//        barDataSet.valueTextColor = Color.BLACK
//        barDataSet.valueTextSize = 6f
//        barDataSet.colors = colors
//        val barData = BarData(barDataSet)
//        barData.barWidth = 0.5f
//
//
//
//        barChart.apply {
//            setDrawValueAboveBar(true)
//            extraBottomOffset = 10f
//            axisLeft.isEnabled = false
//            axisRight.isEnabled = false
//            description.isEnabled = false
//            xAxis.labelRotationAngle = rotationAngle
//            xAxis.setDrawGridLines(false)
//            xAxis.setDrawAxisLine(true)
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
//            xAxis.labelCount = labels.size
//            xAxis.textSize = 7f
//            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//            legend.isEnabled = false
//            setTouchEnabled(false)
//            data = barData
//            animateY(800)
//            invalidate()
//        }
//
//    }
private fun showBarChart(horizontalGraphModel: HorizontalGraphModel, barChart: BarChart) {

    val valueList = ArrayList<Double>()
    val entries: ArrayList<BarEntry> = ArrayList()
    val colors = ArrayList<Int>()
    val labels = ArrayList<String>()

    horizontalGraphModel.barChartModel.forEachIndexed { index, chartData ->
        val barEntry = BarEntry(index.toFloat(), chartData.value)
        entries.add(barEntry)
        colors.add(Utils.parseColorSafely(chartData.valueColor))
        labels.add(chartData.key)
    }

    val barDataSet = BarDataSet(entries, "")
    barDataSet.setDrawValues(true)
    barDataSet.valueTextColor = Color.BLACK
    barDataSet.valueTextSize = 6f
    barDataSet.colors = colors

    val barData = BarData(barDataSet)
    barData.barWidth = 0.3f // Adjusted bar width to reduce overlap

    barChart.apply {
        setDrawValueAboveBar(true)
        setPadding(0, 0, 0, 0)
        extraRightOffset = 0f
        extraLeftOffset = 0f
        extraTopOffset = 0f
        extraBottomOffset = 15f
        axisLeft.isEnabled = false
        axisRight.isEnabled = false
        description.isEnabled = false
        xAxis.labelRotationAngle = rotationAngle
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
        xAxis.labelCount = labels.size
        xAxis.granularity = 1f // Ensures each label is only drawn once
        xAxis.textSize = 7f
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        legend.isEnabled = false
        setTouchEnabled(false)
        data = barData
        animateY(800)
        invalidate()
    }
}

    private fun setupLegend() {
        val legend: Legend = binding.barChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Increase", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#6348A0")
        )
        val l2 = LegendEntry(
            "Decrease", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F")
        )
        val l3 = LegendEntry(
            "Net Off", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#FFC400")
        )
        legend.setCustom(arrayOf(l1, l2, l3))
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.footerGraph.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        hideProgressIndicator()
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                        Toast.makeText(
                            context,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {
                        showProgressIndicator()
                    }

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
                        val responseBody = it.data?.body()
                        if (responseBody?.asJsonArray?.isEmpty == true){
                            binding.barChart.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }
                        val recyclerViewItems: ArrayList<String> = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel.add(
                                Gson().fromJson(
                                    jsonArray,
                                    HorizontalGraphModel::class.java
                                )
                            )
                            graphModel[index].label?.let { it1 -> recyclerViewItems.add(it1) }
                        }

                        setupRecyclerView(recyclerViewItems)

                        if (graphModel.isNotEmpty())  showBarChart(graphModel[0], binding.barChart)

                    }
                }
            }
        }
    }

    private fun setupRecyclerView(listItems: ArrayList<String>) {
        if (listItems.size == 1) binding.recyclerView.visibility = View.GONE
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
        showBarChart(graphModel[position], binding.barChart)

    }

    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.barChart.visibility = View.GONE

    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.barChart.visibility = View.VISIBLE
    }
}