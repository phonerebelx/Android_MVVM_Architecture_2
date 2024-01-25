package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.FragmentTopBottomBranchesBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.InvertedHorizontalGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HorizontalBarFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentTopBottomBranchesBinding
    private lateinit var adapter: BarChartAdapter
    private val myViewModel: DashboardViewModel by viewModel()
    private val graphModel: ArrayList<InvertedHorizontalGraphModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopBottomBranchesBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()

        return binding.root
    }

    private fun showBarChart(
        chartModelTop: ArrayList<HorizontalBarChartDataModel>,
        chartModelBottom: ArrayList<HorizontalBarChartDataModel>,
        horizontalBarChart: HorizontalBarChart
    ) {
        val entries: ArrayList<BarEntry> = ArrayList()
        val myColors = ArrayList<Int>()
        val labels: ArrayList<String> = arrayListOf()

        var lastIndex = 0f
        chartModelTop.forEachIndexed { index, dataModel ->
            entries.add(BarEntry(index.toFloat(), dataModel.value))
            myColors.add(Utils.parseColorSafely(dataModel.valueColor))
            labels.add(dataModel.key)
            lastIndex = index.toFloat()
        }
        var updatedIndex = lastIndex + 1
        for (dataModel in chartModelBottom) {
            entries.add(BarEntry(updatedIndex, dataModel.value))
            myColors.add(Utils.parseColorSafely(dataModel.valueColor))
            labels.add(dataModel.key)
            updatedIndex++
        }

        val barDataSet = BarDataSet(entries, "Target")

        barDataSet.colors = myColors

//        val totalSum = valueList.sum()
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
//                val percentage = (abs(value.toInt()) / totalSum) * 100
//                val formatted = "%.1f%%".format(percentage)
                return value.toInt().toString()
            }
        }

        barDataSet.setDrawValues(true)
        barDataSet.valueTextSize = 8f
        barDataSet.valueTextColor = Color.WHITE

        val mData = BarData(barDataSet)
        mData.barWidth = 0.7f
        mData.isHighlightEnabled = false

        //Display the axis on the left
        val xAxis = horizontalBarChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(false)
        xAxis.labelCount = entries.size

        xAxis.valueFormatter = object : IndexAxisValueFormatter(labels) {
            override fun getFormattedValue(value: Float): String {
                return if (value >= 0 && value.toInt() < labels.size) {
                    labels[value.toInt()]
                } else {
                    ""
                }
            }
        }

        val maxValue = entries.maxByOrNull { it.y }
        val minValue = entries.minByOrNull { it.y }

        horizontalBarChart.apply {
            axisLeft.axisMaximum = maxValue?.y!!
            //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
            axisLeft.axisMinimum = minValue?.y!!
            axisRight.axisMinimum = minValue.y
            axisRight.axisMaximum = maxValue.y
            legend.isEnabled = false
            setDrawValueAboveBar(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
            description.isEnabled = false
            invalidate()
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

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        val responseBody = it.data?.body()
                        val listItems: ArrayList<String> = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel.add(
                                Gson().fromJson(
                                    jsonArray,
                                    InvertedHorizontalGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it2 -> listItems.add(it2) }
                        }
                        setupRecyclerView(listItems)

                        showBarChart(
                            graphModel[0].top,
                            graphModel[0].bottom,
                            binding.horizontalBarChart
                        )

                    }
                }
            }
        }
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        showBarChart(
            graphModel[position].top,
            graphModel[position].bottom,
            binding.horizontalBarChart
        )
    }


}