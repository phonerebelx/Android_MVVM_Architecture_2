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
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.FragmentDepositTrendBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LineChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentDepositTrendBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private lateinit var adapter: BarChartAdapter
    private val graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositTrendBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()
        return binding.root
    }

    private fun drawLineChart(tierGraphModel: HorizontalGraphModel) {

        val lineEntries = listOf(
            Entry(2016F, 0F),
            Entry(2017F, 25000F),
            Entry(2018F, 0F),
            Entry(2019F, 27000F),
            Entry(2020F, 28000F),
            Entry(2021F, 35000F),
            Entry(2022F, 40000F)
        )
        val lineEntries2 = listOf(
            Entry(2016F, 15000F),
            Entry(2017F, 27000F),
            Entry(2018F, 7F),
            Entry(2019F, 22000F),
            Entry(2020F, 0F),
            Entry(2021F, 30000F),
            Entry(2022F, 34000F)
        )

        val lineDataSet = LineDataSet(lineEntries, "")
        val lineDataSet2 = LineDataSet(lineEntries2, "")

        customizationLine(lineDataSet, Color.parseColor("#755AB2"))
        customizationLine(lineDataSet2, Color.parseColor("#FCD243"))

        val lineData = LineData(listOf(lineDataSet, lineDataSet2))

        binding.lineChart.apply {
            description.isEnabled = false
            setDrawMarkers(false)
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1.0f
            xAxis.labelCount = lineDataSet.entryCount
            data = lineData
        }

    }

    private fun customizationLine(lineDataSet: LineDataSet, color: Int) {
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.lineWidth = 2f
        lineDataSet.color = color
        lineDataSet.setCircleColor(color)
        lineDataSet.circleRadius = 5f
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.valueTextSize = 12f
        lineDataSet.valueTextColor = Color.DKGRAY
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
                                    HorizontalGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it1 ->
                                if (it1 != null) {
                                    recyclerViewItems.add(it1)
                                }
                            }
                        }

                        setupRecyclerView(recyclerViewItems)

                        drawLineChart(graphModel[0])

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

    override fun onClick(item: String?, position: Int) {

    }


}