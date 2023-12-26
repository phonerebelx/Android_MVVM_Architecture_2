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
import com.example.meezan360.databinding.FragmentMomTargetVsAchievementBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class BarChartFragment(
    var kpiId: Int?,
    private var tagName: String,
    private var dataModel: DataModel
) : Fragment() {

    private lateinit var binding: FragmentMomTargetVsAchievementBinding
    private val myViewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentMomTargetVsAchievementBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()
//        showCombineChart()
        return binding.root
    }

    private fun showCombineChart() {
        // Sample data for the bar chart
        val valueList = listOf(
            20.0, 45.0, 15.0, 25.0, 35.0, 25.0, 35.0, 35.0, 25.0, 38.0, 30.0, 38.0
        )

        val entries: ArrayList<BarEntry> = ArrayList()
        for (i in valueList.indices) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "Target")

        val startColor = Color.parseColor("#1F753E")
        val endColor = Color.parseColor("#3A9F5E")
        barDataSet.setGradientColor(startColor, endColor)

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f

        val labels = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        // Scatter chart (to place squares on top of each bar)
        val scatterEntries = ArrayList<Entry>()
        for (i in valueList.indices) {
            scatterEntries.add(
                Entry(
                    i.toFloat(), valueList[i].toFloat() + 10
                )
            ) // Use the same values as bars
        }

        val scatterDataSet = ScatterDataSet(scatterEntries, "Scatter Chart")
//        scatterDataSet.shapeRenderer = IShapeRenderer()
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        scatterDataSet.scatterShapeSize = 16f // Adjust size as needed
        scatterDataSet.color = Color.parseColor("#E8544F") // Set the color for the squares

        val scatterData = ScatterData(scatterDataSet)
        scatterData.setDrawValues(false)

        // Combine bar and scatter data
        val combineData = CombinedData()
        combineData.setData(barData)
        combineData.setData(scatterData)

        // Legend and chart settings...
        val legend: Legend = binding.combineChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Target", Legend.LegendForm.SQUARE, 8f, 0f, null, Color.parseColor("#DB4336")
        )
        val l2 = LegendEntry(
            "Achievement", Legend.LegendForm.DEFAULT, 8f, 0f, null, Color.parseColor("#3A9F5E")
        )
        legend.setCustom(arrayOf(l1, l2))

        binding.combineChart.apply {
            extraBottomOffset = 10f
            description.isEnabled = false
            xAxis.spaceMin = barData.barWidth / 2f // First bar to show properly
            xAxis.spaceMax = barData.barWidth / 2f // Last bar to show properly
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = valueList.size
            xAxis.textSize = 7f
            xAxis.textColor = Color.parseColor("#676767")
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = combineData
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
//                        Toast.makeText(
//                            context,
//                            "success",
//                            Toast.LENGTH_SHORT
//                        ).show()

                    }
                }
            }
        }

    }


}