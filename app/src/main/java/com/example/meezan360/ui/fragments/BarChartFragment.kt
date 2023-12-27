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
import com.example.meezan360.databinding.FragmentMomTargetVsAchievementBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class BarChartFragment(
    var kpiId: Int?,
    private var tagName: String,
    private var dataModel: DataModel
) : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentMomTargetVsAchievementBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private lateinit var adapter: BarChartAdapter
    private var listItems: ArrayList<TopBoxesModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentMomTargetVsAchievementBinding.inflate(layoutInflater)

        setupRecyclerView()

        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()
//        showCombineChart()
        return binding.root
    }

    private fun setupRecyclerView() {
        listItems.add(TopBoxesModel(title = "CA"))
        listItems.add(TopBoxesModel(title = "SA"))
        listItems.add(TopBoxesModel(title = "CASA"))
        listItems.add(TopBoxesModel(title = "TD"))
        listItems.add(TopBoxesModel(title = "Total"))

        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        adapter = BarChartAdapter(listItems, this)
        binding.recyclerView.adapter = adapter
    }

    private fun showCombineChart(
        barChartModel: ArrayList<HorizontalBarChartDataModel>,
        combineChart: CombinedChart
    ) {
        val entries: ArrayList<BarEntry> = ArrayList()
        val scatterEntries = ArrayList<Entry>()
        val labels = ArrayList<String>()
        val colorsBar = ArrayList<Int>()
        val colorsTarget = ArrayList<Int>() //for squares

        for (index in barChartModel.indices) {
            entries.add(BarEntry(index.toFloat(), barChartModel[index].value.toFloat()))
            scatterEntries.add(Entry(index.toFloat(), barChartModel[index].target.toFloat()))
            labels.add(barChartModel[index].key)
            colorsBar.add(Color.parseColor(barChartModel[index].valueColor))
            colorsTarget.add(Color.parseColor(barChartModel[index].targetColor))
        }

        val barDataSet = BarDataSet(entries, "Target")
        barDataSet.colors = colorsBar
        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f


        // Scatter chart (to place squares on top of each bar)
        val scatterDataSet = ScatterDataSet(scatterEntries, "Scatter Chart")
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        scatterDataSet.scatterShapeSize = 16f // Adjust size as needed
        scatterDataSet.colors = colorsTarget // Set the color for the squares

        val scatterData = ScatterData(scatterDataSet)
        scatterData.setDrawValues(false)

        // Combine bar and scatter data
        val combineData = CombinedData()
        combineData.setData(barData)
        combineData.setData(scatterData)

        // Legend and chart settings...
        val legend: Legend = combineChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Target", Legend.LegendForm.SQUARE, 8f, 0f, null, colorsTarget[0]
        )
        val l2 = LegendEntry(
            "Achievement", Legend.LegendForm.DEFAULT, 8f, 0f, null, colorsBar[0]
        )
        legend.setCustom(arrayOf(l1, l2))

        combineChart.apply {
            setTouchEnabled(false)
            extraBottomOffset = 10f
            description.isEnabled = false
            xAxis.spaceMin = barData.barWidth / 2f // First bar to show properly
            xAxis.spaceMax = barData.barWidth / 2f // Last bar to show properly
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = entries.size
            xAxis.textSize = 7f
            xAxis.textColor = Color.parseColor("#676767")
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = combineData
            invalidate()
        }
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
                        val jsonArray = responseBody?.asJsonArray?.get(0).toString()

                        //recycler view here //get all labels and to send to recycler view
                        //position

//                        val jsonArray = responseBody?.asJsonArray?.get(0).toString()

                        val graphModel: HorizontalGraphModel? = try {
                            Gson().fromJson(jsonArray, HorizontalGraphModel::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }
                        if (graphModel?.barChartModel != null) {
                            showCombineChart(graphModel.barChartModel, binding.combineChart)
                        }
                    }
                }
            }
        }

    }

    override fun onClick(item: TopBoxesModel?, position: Int) {

    }


}