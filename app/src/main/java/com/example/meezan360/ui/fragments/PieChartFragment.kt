package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.meezan360.R
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentDepositCompositionBinding
import com.example.meezan360.databinding.FragmentPieChartBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.example.meezan360.model.footerGraph.SinglePieModel.Graph1
import com.example.meezan360.model.graphs.Pie2Bar2Model
import com.example.meezan360.model.footerGraph.SinglePieModel.PieChartModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PieChartFragment() : BaseDockFragment() {
    private lateinit var binding: FragmentPieChartBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private var kpiId: Int? = null
    private var tagName: String? = null
    private var dataModel: DataModel? = null

    constructor(
        kpiId: Int?, tagName: String, dataModel: DataModel
    ) : this() {
        this.kpiId = kpiId
        this.tagName = tagName
        this.dataModel = dataModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPieChartBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel?.cardTitle
        myViewModel.viewModelScope.launch {
            dataModel?.cardId?.let {
                tagName?.let { it1 ->
                    myViewModel.getFooterGraphs(
                        kpiId.toString(),
                        it1, it
                    )
                }
            }
        }
        handleAPIResponse()
        return binding.root
    }

    private fun showPieChart(graph1: Graph1?, pieChart: PieChart) {

        graph1?.let {
            val pieEntryValue = graph1.data.value
            val pieEntryValue1 = graph1.data.value1
            val pieEntryValue2 = graph1.data.value2

            val pieEntries = mutableListOf<PieEntry>()
            pieEntries.add(PieEntry(pieEntryValue1.toFloat()))
            pieEntries.add(PieEntry(pieEntryValue2.toFloat()))

            val colors: ArrayList<Int> = ArrayList()
            colors.add(Color.parseColor(graph1.data.value1_color))
            colors.add(Color.parseColor(graph1.data.value2_color))

            val legendEntries = ArrayList<LegendEntry>()
            for (legend in graph1.legend) {
                val legendEntry = LegendEntry()
                legendEntry.label = legend.legend_value
                legendEntry.formColor = Color.parseColor(legend.legend_color)
                legendEntries.add(legendEntry)
            }

            val legend = pieChart.legend
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.setDrawInside(false)
            legend.textSize = 12f
            legend.setCustom(legendEntries.reversed())
            val pieDataSet = PieDataSet(pieEntries, "")
            pieDataSet.valueTextSize = 0f
            pieDataSet.colors = colors
            pieDataSet.selectionShift = 0f
            pieChart.apply {
                description.isEnabled = false
                centerText = "$pieEntryValue%"
                setCenterTextColor(Color.parseColor("#765CB4"))
                setCenterTextSize(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp).toFloat())
                setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.montserrat_regular))
                setHoleColor(Color.parseColor("#FFFFFF"))
                holeRadius = 80f
                setTouchEnabled(false)
                data = PieData(pieDataSet)
                animateY(1000)
                invalidate()
            }
        }
    }


    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.pieChartRatio.visibility = View.GONE

    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.pieChartRatio.visibility = View.VISIBLE
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

                        val pieChartModel: PieChartModel? = try {
                            Gson().fromJson(responseBody, PieChartModel::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }
                        if (pieChartModel != null) {


                            if (pieChartModel.graph1 == null) {
                                binding.pieChartRatio.visibility = View.GONE
                                binding.tvView.visibility = View.VISIBLE
                            }
                        }
                        showPieChart(pieChartModel?.graph1, binding.pieChartRatio)


                    }
                }
            }
        }

    }
}