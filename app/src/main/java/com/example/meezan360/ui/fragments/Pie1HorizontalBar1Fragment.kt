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
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.example.meezan360.model.graphs.Pie1HorizontalBar1Model
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.CustomMarker
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

    private fun showHorizontalBarChart(
        graph2: HorizontalGraphModel?,
        horizontalBarChart: HorizontalBarChart
    ) {
        graph2?.let {
            val labels: ArrayList<String> = arrayListOf()
            val entries: ArrayList<BarEntry> = arrayListOf()
            val colors: ArrayList<Int> = arrayListOf()
            val percentages: ArrayList<Float> = arrayListOf()

            graph2.barChartModel.forEachIndexed { index, barChartModel ->
                labels.add(barChartModel.key)
                entries.add(BarEntry(index.toFloat(), barChartModel.value))
                colors.add(Color.parseColor(barChartModel.valueColor))
                barChartModel.percentage?.let { percentages.add(it) }
            }


            val barDataSet = BarDataSet(entries, "Target")
            barDataSet.colors = colors

            //for text inside horizontal bars
            var index = 0
            barDataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (index < percentages.size) {
                        val myPercentage = percentages[index]
                        index++
                        return "${value.toInt()}($myPercentage%)"
                    }
                    return ""
                }
            }

            barDataSet.setDrawValues(true)
            barDataSet.valueTextSize = 8f
            barDataSet.valueTextColor = Color.WHITE

            val mData = BarData(barDataSet)
            mData.barWidth = 0.7f
            mData.isHighlightEnabled = false

            //for text on left side
            val xl: XAxis = horizontalBarChart.xAxis
            xl.position = XAxis.XAxisPosition.BOTTOM
            xl.setDrawAxisLine(true)
            xl.setDrawGridLines(false)
            xl.valueFormatter = object : IndexAxisValueFormatter(labels) {
                override fun getFormattedValue(value: Float): String {
                    return if (value >= 0 && value.toInt() < labels.size) {
                        labels[value.toInt()]
                    } else {
                        ""
                    }
                }
            }
            xl.granularity = 1f

            setupLegend()

            val axisMax = entries.maxOfOrNull {
                it.y
            }

            horizontalBarChart.apply {
                setTouchEnabled(false)
                marker = CustomMarker(context, R.layout.marker_layout) //
                if (axisMax != null) {
                    axisLeft.axisMaximum = axisMax + 2000
                } //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
                axisLeft.axisMinimum = 0f
                setDrawValueAboveBar(false)
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                data = mData
                description.isEnabled = false
                invalidate()
            }
        }
    }


    private fun setupLegend() {
        val legend: Legend = binding.horizontalBarChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Target", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F")
        )
        legend.setCustom(arrayOf(l1))
    }

    private fun showPieChart(graph1: PieGraphModel?, pieChart: PieChart) {

        graph1?.let {
            val pieEntryValueCA = graph1.pieChartModel.value
            val pieEntries = mutableListOf<PieEntry>()

            pieEntries.add(PieEntry(pieEntryValueCA))
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
                centerText = "${pieEntryValueCA}%"
                extraLeftOffset = 25f
                setTouchEnabled(false) //to stop rotation
                setHoleColor(Color.parseColor("#E0E0E0"))
                setCenterTextSize(8f)
                setCenterTextColor(Color.parseColor("#7B7878"))
                data = PieData(pieDataSet)
                invalidate()
            }
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

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {

                        val responseBody: String = it.data?.body()?.toString() ?: ""

                        val pie1Bar1Model: Pie1HorizontalBar1Model? = try {
                            Gson().fromJson(responseBody, Pie1HorizontalBar1Model::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }

                        showPieChart(pie1Bar1Model?.graph1, binding.pieChart)
                        showHorizontalBarChart(
                            pie1Bar1Model?.graph2,
                            binding.horizontalBarChart
                        )

                    }
                }
            }
        }

    }


}