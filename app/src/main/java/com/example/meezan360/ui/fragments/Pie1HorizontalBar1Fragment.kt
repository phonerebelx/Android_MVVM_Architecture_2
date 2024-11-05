package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentTargetVsAchievementBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.example.meezan360.model.graphs.Pie1HorizontalBar1Model
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.CustomMarker
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class Pie1HorizontalBar1Fragment(
    private var kpiId: Int?,
    private var tagName: String,
    private var dataModel: DataModel
) : BaseDockFragment() {

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
            val targetEntries: ArrayList<BarEntry> = arrayListOf()
            val colors: ArrayList<Int> = arrayListOf()
            val targetColors: ArrayList<Int> = arrayListOf()
            val percentages: ArrayList<Float> = arrayListOf()

            // Process data in reverse order for correct display
            for (index in graph2.barChartModel.indices.reversed()) {
                val barChartModel = graph2.barChartModel[index]
                val percentage = barChartModel.percentage ?: 0f
                val target = 100f

                labels.add(barChartModel.key)
                entries.add(BarEntry((graph2.barChartModel.size - 1 - index).toFloat(), percentage))
                targetEntries.add(BarEntry((graph2.barChartModel.size - 1 - index).toFloat(), target))

                colors.add(Utils.parseColorSafely(barChartModel.valueColor))
                targetColors.add(Utils.parseColorSafely(barChartModel.targetColor))
                percentages.add(percentage)
            }

            val barDataSet = BarDataSet(entries, "Value")
            barDataSet.colors = colors

            val barDataSet2 = BarDataSet(targetEntries, "Target (100%)")
            barDataSet2.colors = targetColors
            barDataSet2.setDrawValues(false)

            // ValueFormatter that directly uses the entry's Y value (which is the percentage)
            barDataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.2f%%", value)
                }
            }

            barDataSet.setDrawValues(true)
            barDataSet.valueTextSize = 8f
            barDataSet.valueTextColor = Color.WHITE

            // Configure X Axis
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

            // Set up BarData
            val barData = BarData(barDataSet2, barDataSet)
            barData.barWidth = 0.7f
            barData.isHighlightEnabled = false

            // Dynamically calculate axis maximum
            val maxEntryValue = maxOf(
                entries.maxOfOrNull { it.y } ?: 0f,
                targetEntries.maxOfOrNull { it.y } ?: 100f
            )

            // Apply chart settings
            horizontalBarChart.apply {
                setPadding(0, 0, 0, 0)
                extraRightOffset = 0f
                extraLeftOffset = 0f
                data = barData
                setTouchEnabled(false)
                axisLeft.apply {
                    axisMaximum = maxEntryValue
                    axisMinimum = 0f
                    isEnabled = true
                    setDrawAxisLine(false)
                    setDrawLabels(false)
                    setDrawGridLines(false)
                }
                axisRight.apply {
                    isEnabled = true
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    setDrawLabels(false)
                }
                setDrawValueAboveBar(false)
                description.isEnabled = false
                val legend: Legend = horizontalBarChart.legend
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
                legend.xEntrySpace = 25f
                val l1 = LegendEntry(
                    "Achievement", Legend.LegendForm.SQUARE, 8f, 0f, null, colors[0]
                )
                val l2 = LegendEntry(
                    "Target", Legend.LegendForm.DEFAULT, 8f, 0f, null, targetColors[0]
                )
                legend.setCustom(arrayOf(l1, l2))

                animateXY(1000, 800)
                invalidate()
            }
        }
    }

    private fun showPieChart(graph1: PieGraphModel?, pieChart: PieChart) {

        graph1?.let {
            // Get the value from the model
            val actualValue = graph1.pieChartModel.value

            // Clamp the value to 100 if it exceeds
            val pieEntryValueCA = if (actualValue > 100) 100f else actualValue

            val pieEntries = mutableListOf<PieEntry>()
            pieEntries.add(PieEntry(pieEntryValueCA))
            pieEntries.add(PieEntry(100 - pieEntryValueCA))

            val colors: ArrayList<Int> = ArrayList()
            colors.add(Utils.parseColorSafely(graph1.pieChartModel.color))
            colors.add(Color.parseColor("#FAFAFA"))

            val pieDataSet = PieDataSet(pieEntries, "")
            pieDataSet.valueTextSize = 0f
            pieDataSet.colors = colors
            pieDataSet.selectionShift = 0f

            pieChart.apply {
                legend.isEnabled = false
                description.isEnabled = false
                centerText = "${actualValue}%"  // Show the actual percentage in the center
                extraLeftOffset = 15f
                setTouchEnabled(false)
                setHoleColor(Color.parseColor("#FFFFFF"))
                setCenterTextSize(12f)
                holeRadius = 70f
                setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.montserrat_regular))
                setCenterTextColor(Color.parseColor("#765CB4"))

                data = PieData(pieDataSet)
                animateY(1000)
                invalidate()
            }
        }
    }



    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.footerGraph.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        hideProgressIndicator()
                        (requireActivity() as DockActivity).handleErrorResponse(myDockActivity!!,it)
                    }

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {
                        showProgressIndicator()
                    }

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
                        val responseBody: String = it.data?.body()?.toString() ?: ""

                        val pie1Bar1Model: Pie1HorizontalBar1Model? = try {
                            Gson().fromJson(responseBody, Pie1HorizontalBar1Model::class.java)
                        } catch (e: JsonSyntaxException) {
                            binding.horizontalBarChart.visibility = View.GONE
                            binding.pieChart.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                            null
                        }

                        if ( pie1Bar1Model?.graph1 != null){
                            showPieChart(pie1Bar1Model?.graph1, binding.pieChart)
                        }else{
                            binding.pieChart.visibility = View.GONE
                        }

                        if ( pie1Bar1Model?.graph2 != null){
                            showHorizontalBarChart(
                                pie1Bar1Model?.graph2,
                                binding.horizontalBarChart
                            )
                        }else{
                            binding.horizontalBarChart.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }


                    }
                }
            }
        }

    }
    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.pieChart.visibility = View.GONE
        binding.horizontalBarChart.visibility = View.GONE

    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.pieChart.visibility =  View.VISIBLE
        binding.horizontalBarChart.visibility =  View.VISIBLE
    }

}