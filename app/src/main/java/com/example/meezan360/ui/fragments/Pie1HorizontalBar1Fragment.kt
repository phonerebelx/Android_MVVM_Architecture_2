package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentTargetVsAchievementBinding
import com.example.meezan360.model.graphs.Pie1HorizontalBar1Model
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class Pie1HorizontalBar1Fragment(
    private var kpiId: Int?,
    private var tagName: String,
    private var dataModel: DataModel
) : Fragment() {

    private lateinit var binding: FragmentTargetVsAchievementBinding
    private val myViewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTargetVsAchievementBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()

        return binding.root
    }

    private fun showBarChart(
        graph2: HorizontalGraphModel?,
        horizontalBarChart: HorizontalBarChart
    ) {

        val entries: ArrayList<BarEntry> = arrayListOf()
        val colors: ArrayList<Int> = arrayListOf()

        graph2?.barChartModel?.forEachIndexed { index, horizontalBarChartDataModel ->
            entries.add(BarEntry(index.toFloat(),graph2.barChartModel[index].value.toFloat()))
            colors.add(Color.parseColor(graph2.barChartModel[index].valueColor))
        }

        val barDataSet = BarDataSet(entries, "Target")
        barDataSet.colors = colors

        val totalSum = entriesList.sum()
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val percentage = (value / totalSum) * 100
                val formatted = "%.1f%%".format(percentage)
                return "${value.toInt()}($formatted)"
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
        xAxis.labelCount = 4

        val labels = setupLegend()

        horizontalBarChart.apply {
            axisLeft.axisMaximum =
                2698483f //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
            axisLeft.axisMinimum = 0f
            setDrawValueAboveBar(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
            description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            invalidate()
        }
    }

    private fun setupLegend(): Array<String> {
        val legend: Legend = binding.horizontalBarChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Target", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F")
        )
        legend.setCustom(arrayOf(l1))

        return arrayOf("Term Deposit", "CASA", "Saving", "Current")
    }

    private fun showPieChart(graph1: PieGraphModel?, pieChart: PieChart) {

        val pieEntryValueCA = graph1?.pieChartModel?.value
        val pieEntries = mutableListOf<PieEntry>()

        pieEntries.add(PieEntry(pieEntryValueCA!!))
        pieEntries.add(PieEntry(100 - pieEntryValueCA))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor(graph1.pieChartModel.color))
        colors.add(Color.parseColor("#FAFAFA"))

        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.valueTextSize = 0f
        pieDataSet.colors = colors
        pieDataSet.selectionShift = 0f

        pieChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            centerText = "75%"
            extraLeftOffset = 25f
            setHoleColor(Color.parseColor("#E0E0E0"))
            setCenterTextSize(12f)
            setCenterTextColor(Color.parseColor("#7B7878"))
            data = PieData(pieDataSet)
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
//                        Toast.makeText(
//                        context,
//                        "Loading..",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    is ResponseModel.Success -> {

                        val responseBody: String = it.data?.body()?.toString() ?: ""

                        val pie1Bar1Model: Pie1HorizontalBar1Model? = try {
                            Gson().fromJson(responseBody, Pie1HorizontalBar1Model::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }

                        showPieChart(pie1Bar1Model?.graph1, binding.pieChart)
                        showBarChart(pie1Bar1Model?.graph2, binding.horizontalBarChart)
                    }
                }
            }
        }

    }


}