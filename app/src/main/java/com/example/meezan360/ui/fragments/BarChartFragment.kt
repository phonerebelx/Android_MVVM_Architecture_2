package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.FragmentMomTargetVsAchievementBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
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
    private val graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()
    private var rotationAngle: Float = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentMomTargetVsAchievementBinding.inflate(layoutInflater)

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

    private fun setupRecyclerView(listItems: ArrayList<String>) {

        if (listItems.size == 1){
            binding.recyclerView.visibility = View.GONE
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        adapter = BarChartAdapter(requireContext(), listItems, this)
        binding.recyclerView.adapter = adapter

    }

    private fun showCombineChart(
        barChartModel: ArrayList<HorizontalBarChartDataModel>,
        combineChart: CombinedChart
    ) {

        if (barChartModel.isEmpty()){
            return
        }

        val entries: ArrayList<BarEntry> = ArrayList()
        val scatterEntries = ArrayList<Entry>()
        val labels = ArrayList<String>()
        val colorsBar = ArrayList<Int>()
        val colorsTarget = ArrayList<Int>() //for squares

        for (index in barChartModel.indices) {

            entries.add(BarEntry(index.toFloat(), barChartModel[index].value))
            barChartModel[index].target?.let { Entry(index.toFloat(), it) }
                ?.let { scatterEntries.add(it) }
            labels.add(barChartModel[index].key)
            colorsBar.add(Utils.parseColorSafely(barChartModel[index].valueColor))
            colorsTarget.add(Utils.parseColorSafely(barChartModel[index].targetColor))
        }

        val barDataSet = BarDataSet(entries, "Target")
        barDataSet.colors = colorsBar
        barDataSet.setDrawValues(false)
        val barData = BarData(barDataSet)
        if (entries.size > 5){
            barData.barWidth = 0.7f
        }else if (entries.size > 4){
            barData.barWidth = 0.1f
        }else{
            barData.barWidth = 0.2f
        }

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
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
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
            xAxis.spaceMin = barData.barWidth / 2f
            xAxis.spaceMax = barData.barWidth / 2f
            xAxis.labelRotationAngle = rotationAngle
            xAxis.position = XAxis.XAxisPosition.BOTTOM


            xAxis.setLabelCount(labels.size,false)
            xAxis.granularity = 1f
            xAxis.valueFormatter =  object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value >= 0) {
                        if (value <= labels.size - 1) {
                            return labels[value.toInt()]
                        }
                        return ""
                    }
                    return ""
                }
            }
            xAxis.textSize = 7f
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.black)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f

            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = combineData
            animateY(800)
            invalidate()
        }
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
                                    HorizontalGraphModel::class.java
                                )
                            )
                            graphModel[index].label?.let { it1 -> listItems.add(it1) }
                        }
                        setupRecyclerView(listItems)

                        if (graphModel.isNotEmpty()) showCombineChart(graphModel[0].barChartModel, binding.combineChart)

                    }
                }
            }
        }
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        showCombineChart(graphModel[position].barChartModel, binding.combineChart)
    }

}



