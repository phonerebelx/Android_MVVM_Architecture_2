package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.FragmentOnOffBranchesBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.StackGraphModel
import com.example.meezan360.model.footerGraph.data.MyLegend
import com.example.meezan360.network.ResponseModel
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


class StackChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentOnOffBranchesBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private val graphModel: ArrayList<StackGraphModel> = arrayListOf()
    private lateinit var adapter: BarChartAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnOffBranchesBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse(dataModel.cardTypeId)

        binding.switchTD.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                showBarChart(
                    graphModel[0],
                    binding.barChart,
                    true
                )
            else
                showBarChart(graphModel[0], binding.barChart, false)
            //when there is switch the graphmodel will always have one main object
        }

        return binding.root
    }

    private fun showBarChart(
        stackChartModel: StackGraphModel,
        barChart: BarChart,
        switchTD: Boolean
    ) {

        val entries: ArrayList<BarEntry> = arrayListOf()
        val colors: ArrayList<Int> = arrayListOf()
        val labels = ArrayList<String>()
        var tdIndex: Int? = null
        stackChartModel.stackChartData.forEachIndexed { i, stackChartDataModel ->
            //for TD switch
            var index: Int? = null
            if (stackChartDataModel.key.equals("term", true) && !switchTD) {
                tdIndex = i
            } else {
                index = i
            }
            if (index != null) {
                val barEntry = BarEntry(
                    index.toFloat(),
                    floatArrayOf(
                        stackChartModel.stackChartData[index].value1.toFloat(),
                        stackChartModel.stackChartData[index].value2.toFloat()
                    )
                )
                entries.add(barEntry)
                colors.add(Color.parseColor(stackChartModel.stackChartData[index].value1Color))
                colors.add(Color.parseColor(stackChartModel.stackChartData[index].value2Color))
                labels.add(stackChartModel.stackChartData[index].key)
            }
        }


//        //for switch
//        if (!switchTD) {
//
//            val arraytwo = ArrayList<BarEntry>()
//            tdIndex?.let { entries.drop(it) }
//            if (arraytwo != null) {
//                for (i in arraytwo.indices) {
//                    arraytwo.add(
//                        BarEntry(
//                            i.toFloat(), floatArrayOf(
//                                entries.value1.toFloat(),
//                                stackChartDataModel.value2.toFloat()
//                            )
//                        )
//                    )
//                }
//            }
//
//            tdIndex?.let {
//                entries.removeAt(it)
//                labels.removeAt(it)
//                colors.removeAt(it)
//            }
//
//        }

        legendSetup(stackChartModel.legend)

        val barDataSet = BarDataSet(entries, "Target")

        barDataSet.setDrawValues(true)
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 6f
        barDataSet.colors = colors

        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f

        barChart.apply {
            setDrawValueAboveBar(false)
            extraBottomOffset = 10f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
            xAxis.labelCount = entries.size
            xAxis.textSize = 7f
            setTouchEnabled(false)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            data = barData
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

    private fun legendSetup(myList: ArrayList<MyLegend>) {
        val legend: Legend = binding.barChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
        legend.xEntrySpace = 25f
        val legendEntries = ArrayList<LegendEntry>()
        myList.forEachIndexed { index, legend ->
            legendEntries.add(
                LegendEntry(
                    myList[index].legendValue,
                    Legend.LegendForm.CIRCLE,
                    8f,
                    0f,
                    null,
                    Color.parseColor(myList[index].legendColor)
                )
            )
        }
        legend.setCustom(legendEntries)
    }

    private fun handleAPIResponse(cardTypeId: String) {
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
                                    StackGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it1 -> recyclerViewItems.add(it1) }
                        }

                        if (cardTypeId == "4") {
                            setupRecyclerView(recyclerViewItems)
                            binding.switchTD.visibility = View.GONE
                        } else {
                            binding.switchTD.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }

                        showBarChart(graphModel[0], binding.barChart, false)

                    }
                }
            }
        }

    }

    override fun onClick(item: String?, position: Int) {
        showBarChart(graphModel[position], binding.barChart, false)

    }


}