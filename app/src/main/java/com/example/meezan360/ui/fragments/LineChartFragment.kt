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
import com.example.meezan360.adapter.LineChartAdapter
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LineChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentDepositTrendBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private lateinit var adapter: LineChartAdapter
    private val graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()
    private val positionsList = ArrayList<Int>()
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

    private fun drawLineChart(
        tierGraphModel: ArrayList<HorizontalGraphModel>,
        positionsList: ArrayList<Int>
    ) {

        val listOfDataSet: MutableList<LineDataSet> = mutableListOf()

        for (i in positionsList) {
            val lineEntries = ArrayList<Entry>()
            tierGraphModel[i].barChartModel.forEachIndexed { index, dataModel ->
                lineEntries.add(Entry(index.toFloat(), dataModel.value))
            }
//            for (dataModel in tierGraphModel[i].barChartModel) {
//                lineEntries.add(Entry(dataModel.key.toFloat(), dataModel.value))
//            }
            val lineDataSet = LineDataSet(lineEntries, "")
            customizationLine(lineDataSet, Color.parseColor(tierGraphModel[i].color))
            listOfDataSet.add(lineDataSet)
        }

        val lineData = LineData(listOfDataSet as List<ILineDataSet>?)

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
//            xAxis.labelCount = lineEntries.count()
            data = lineData
            invalidate()
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

                        setupRecyclerView(graphModel)

                        positionsList.add(0)//for first line to show
                        drawLineChart(graphModel, positionsList)

                    }
                }
            }
        }

    }

    private fun setupRecyclerView(listItems: ArrayList<HorizontalGraphModel>) {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        adapter = LineChartAdapter(requireContext(), listItems, this)
        binding.recyclerView.adapter = adapter

    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {

        if (checked == true) {
            positionsList.add(position)
        } else {
            positionsList.removeAll {
                it == position
            }
        }
        drawLineChart(graphModel, positionsList)
    }


}