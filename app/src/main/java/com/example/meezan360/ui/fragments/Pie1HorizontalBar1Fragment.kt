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
import androidx.core.content.res.ResourcesCompat
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
            val targetEntries: ArrayList<BarEntry> = arrayListOf()
            val Value: ArrayList<Float> = arrayListOf()
            val targetValue: ArrayList<Float> = arrayListOf()
            val colors: ArrayList<Int> = arrayListOf()
            val targetColors: ArrayList<Int> = arrayListOf()
            val percentages: ArrayList<Float> = arrayListOf()

            for (index in graph2.barChartModel.size - 1 downTo 0) {
                val barChartModel = graph2.barChartModel[index]
                labels.add(barChartModel.key)
                entries.add(BarEntry(((graph2.barChartModel.size - 1) - index).toFloat(), barChartModel.percentage!!))
                targetEntries.add(BarEntry(((graph2.barChartModel.size - 1) - index).toFloat(), 100F))
                Value.add(barChartModel.value)
                targetValue.add(barChartModel.target!!)
                targetEntries.add(BarEntry(((graph2.barChartModel.size - 1) - index).toFloat(), 100F))
                colors.add(Utils.parseColorSafely(barChartModel.valueColor))
                targetColors.add(Utils.parseColorSafely(barChartModel.targetColor))
                barChartModel.percentage?.let { percentages.add(it) }
            }

            val barDataSet = BarDataSet(entries, "Target")
            barDataSet.colors = colors

            val barDataSet2 = BarDataSet(targetEntries, "Target2")
            barDataSet2.colors = targetColors
            barDataSet2.setDrawValues(false)



            var index = 0
            barDataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (index < barDataSet.entryCount) {
                        val value = "${percentages[index]}%"
                        index++
                        return value
                    } else {
                        index = 0
                    }
                    val outsideValue = percentages[index]
                    index++
                    return "${outsideValue}%"
                }
            }


            barDataSet.setDrawValues(true)
            barDataSet.valueTextSize = 8f
            barDataSet.valueTextColor = Color.WHITE



            val yAxis = horizontalBarChart.axisLeft




            val limitLine = LimitLine(100F, "")
            limitLine.lineWidth = 2f
//            limitLine.enableDashedLine(10f, 10f, 0f)
            limitLine.lineColor = ContextCompat.getColor(requireContext(), R.color.red);
            limitLine.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
            yAxis.addLimitLine(limitLine)
            yAxis.setDrawLimitLinesBehindData(false)





            val mData = BarData(barDataSet2, barDataSet)
            mData.barWidth = 0.7f
            mData.isHighlightEnabled = false


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
                marker = CustomMarker(context, R.layout.marker_layout)
                if (axisMax != null) {
                    axisLeft.axisMaximum = axisMax
                }
                axisLeft.axisMinimum = 0f
                setDrawValueAboveBar(false)
                axisLeft.isEnabled = true
                description.isEnabled = false
                axisLeft.setDrawAxisLine(false)
                axisLeft.setDrawLabels(false)
                axisLeft.setDrawGridLines(false)
                axisRight.isEnabled = true
                axisRight.setDrawGridLines(false)
                axisRight.setDrawAxisLine(true)
                axisRight.setDrawLabels(false)
                data = mData
                description.isEnabled = false
                animateY(800)
                invalidate()
            }
        }
    }


    private fun setupLegend() {
        val legend: Legend = binding.horizontalBarChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
        legend.xEntrySpace = 10f
        val l1 = LegendEntry(
            "Target", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F")
        )
        val l2 = LegendEntry(
            "Achievement", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#1F753E")
        )
        legend.setCustom(arrayOf(l1, l2))
    }

    private fun showPieChart(graph1: PieGraphModel?, pieChart: PieChart) {

        graph1?.let {
            val pieEntryValueCA = graph1.pieChartModel.value
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
                centerText = "${pieEntryValueCA}%"
                extraLeftOffset = 25f
                setTouchEnabled(false)
                setHoleColor(Color.parseColor("#FFFFFF"))
                setCenterTextSize(12f)
                holeRadius = 80f
                 setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.montserrat_regular))
//                setCenterTextColor(Color.parseColor("#7B7878"))
                setCenterTextColor(Color.parseColor("#765CB4"))

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
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
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

                        if ( pie1Bar1Model?.graph1 != null){
                            showPieChart(pie1Bar1Model?.graph1, binding.pieChart)
                        }else{
                            binding.pieChart.visibility = View.GONE
                        }
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