package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentDepositCompositionBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.example.meezan360.model.footerGraph.BarGraphModel
import com.example.meezan360.model.graphs.Pie2Bar2Model
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.progress.ProgressDialog
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.uhfsolutions.carlutions.progress.ProgressIndicator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class Pie2Bar2Fragment() : BaseDockFragment() {

    private lateinit var binding: FragmentDepositCompositionBinding

    private val myViewModel: DashboardViewModel by viewModel()
    private var kpiId: Int? = null
    private var tagName: String? = null
    private var dataModel: DataModel? = null
    constructor(
        kpiId: Int?,tagName: String,dataModel: DataModel
    ) : this() {
        this.kpiId = kpiId
        this.tagName = tagName
        this.dataModel = dataModel
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDepositCompositionBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel?.cardTitle
        myViewModel.viewModelScope.launch {
            dataModel?.cardId?.let { tagName?.let { it1 ->
                myViewModel.getFooterGraphs(kpiId.toString(),
                    it1, it)
            } }
        }

        handleAPIResponse()

        return binding.root
    }
    private fun showPieChart(graph1: PieGraphModel?, pieChart: PieChart) {

        graph1?.let {
            try {
                val pieEntryValueCA = graph1.pieChartModel.value

                val pieEntriesCA = mutableListOf<PieEntry>()
                pieEntriesCA.add(PieEntry(pieEntryValueCA))
                pieEntriesCA.add(PieEntry(100 - pieEntryValueCA))

                val colors: ArrayList<Int> = ArrayList()
                colors.add(Utils.parseColorSafely(graph1.pieChartModel.color))
                colors.add(Color.parseColor("#FAFAFA"))

                val pieDataSet = PieDataSet(pieEntriesCA, "")
                pieDataSet.valueTextSize = 0f
                pieDataSet.colors = colors
                pieDataSet.selectionShift = 0f

                pieChart.apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    centerText = "$pieEntryValueCA%"
                    setHoleColor(Color.parseColor("#FFFFFF"))
                    setCenterTextSize(16f)
                    holeRadius = 60f
                    setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.montserrat_regular))
                    setCenterTextColor(Color.parseColor("#7B7878"))
                    setTouchEnabled(false)
                    setCenterTextColor(Color.parseColor(graph1.pieChartModel.color))
                    data = PieData(pieDataSet)


                    animateY(1000)

                    invalidate()
                }
            } catch (e: Exception) {
                null
          }
        }
    }

    private fun showBarChart(graph2: BarGraphModel?, horizontalBarChart: HorizontalBarChart) {

        val labels = ArrayList<String>()
        val yVals1 = ArrayList<BarEntry>()
        val colors: ArrayList<Int> = arrayListOf()

        graph2?.barChartModel?.forEachIndexed { index, _ ->
            labels.add(graph2.barChartModel[index].key.toString())
            yVals1.add(BarEntry(index.toFloat(), graph2.barChartModel[index].value))
            colors.add(Utils.parseColorSafely(graph2.barChartModel[index].valueColor))
        }

        val xl: XAxis = horizontalBarChart.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.textSize = 9f
        xl.textColor = Color.BLACK
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

        val yl: YAxis = horizontalBarChart.axisLeft
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yl.setDrawGridLines(false)
        yl.isEnabled = false
        yl.axisMinimum = 0f
        yl.textColor = Color.WHITE
        val yr: YAxis = horizontalBarChart.axisRight
        yr.isEnabled = false

        val set1 = BarDataSet(yVals1, graph2?.label)
        set1.colors = colors
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}%"
            }
        }

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val barData = BarData(dataSets)

        barData.setValueTextSize(9f)
        barData.setValueTextColor(Color.WHITE)
        barData.barWidth = .7f
        barData.getGroupWidth(3f, 5f)

        horizontalBarChart.apply {
            setDrawBarShadow(false)
            setDrawValueAboveBar(false)
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setPinchZoom(false)
            data = barData
            description.isEnabled = false
            legend.isEnabled = true

            // Custom legend for the chart
            legend.form = Legend.LegendForm.SQUARE
            legend.setCustom(
                arrayOf(
                    LegendEntry(
                        graph2?.label,
                        Legend.LegendForm.SQUARE,
                        10f,
                        Float.NaN,
                        null,
                        set1.color
                    )
                )
            )
            animateXY(1000, 800)

            invalidate()
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
                        val responseBody = it.data?.body()

                        val pie2Bar2Model: Pie2Bar2Model? = try {
                            Gson().fromJson(responseBody, Pie2Bar2Model::class.java)

                        } catch (e: JsonSyntaxException) {
                            null
                        }

                        if (pie2Bar2Model != null) {
                            if (pie2Bar2Model.graph1 == null && pie2Bar2Model.graph2 == null && pie2Bar2Model.graph3 == null && pie2Bar2Model.graph4 == null) {
                                binding.llMain1.visibility = View.GONE
                                binding.llMain2.visibility = View.GONE
                                binding.tvView.visibility = View.VISIBLE
                            }
                            if (pie2Bar2Model.graph1 == null) {
                                binding.pieChartCA.visibility = View.GONE
                            }
                            if (pie2Bar2Model.graph2 == null) {
                                binding.horizontalBarChart.visibility = View.GONE
                            }
                            if (pie2Bar2Model.graph3 == null) {
                                binding.pieChartCASA.visibility = View.GONE
                            }
                            if (pie2Bar2Model.graph4 == null) {
                                binding.horizontalBarChart2.visibility = View.GONE
                            }
                        }


                        try{
                        showPieChart(pie2Bar2Model?.graph1, binding.pieChartCA)
                        showPieChart(pie2Bar2Model?.graph3, binding.pieChartCASA)
                        showBarChart(pie2Bar2Model?.graph2, binding.horizontalBarChart)
                        showBarChart(pie2Bar2Model?.graph4, binding.horizontalBarChart2)
                        }catch (e: Exception){
                            null
                        }

                    }
                }
            }
        }

    }

    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.pieChartCA.visibility = View.GONE
        binding.horizontalBarChart.visibility = View.GONE
        binding.horizontalBarChart2.visibility = View.GONE
        binding.pieChartCASA.visibility = View.GONE
    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.pieChartCA.visibility = View.VISIBLE
        binding.horizontalBarChart.visibility = View.VISIBLE
        binding.horizontalBarChart2.visibility = View.VISIBLE
        binding.pieChartCASA.visibility = View.VISIBLE
    }
}