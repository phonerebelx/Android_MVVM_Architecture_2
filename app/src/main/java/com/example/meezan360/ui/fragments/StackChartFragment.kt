package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
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
import com.example.meezan360.databinding.FragmentOnOffBranchesBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.StackGraphModel
import com.example.meezan360.model.footerGraph.data.MyLegend
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StackChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentOnOffBranchesBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private var graphModel: ArrayList<StackGraphModel>? = null
    private lateinit var adapter: BarChartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnOffBranchesBinding.inflate(inflater, container, false)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse(dataModel.cardTypeId)

        graphModel = arrayListOf() //initialize array
        binding.switchTD.setOnCheckedChangeListener { _, isChecked ->
            graphModel?.get(0)?.let { showBarChart(it, binding.barChart, isChecked) }
        }

        return binding.root
    }

    private fun showBarChart(
        stackChartModel: StackGraphModel,
        barChart: BarChart,
        switchTD: Boolean
    ) {

        val entries: ArrayList<BarEntry> = arrayListOf()
        val colors: ArrayList<Int> = arrayListOf()
        val labels = ArrayList<String>()

        stackChartModel.stackChartData.forEachIndexed { _, stackChartDataModel ->
            if (!(stackChartDataModel.key.equals("term", true) && !switchTD)) {
                val barEntry = BarEntry(
                    entries.size.toFloat(),
                    floatArrayOf(
                        stackChartDataModel.value1.toFloat(),
                        stackChartDataModel.value2.toFloat()
                    )
                )
                entries.add(barEntry)
                colors.add(Utils.parseColorSafely(stackChartDataModel.value1Color))
                colors.add(Utils.parseColorSafely(stackChartDataModel.value2Color))
                labels.add(stackChartDataModel.key)
            }
        }

        legendSetup(stackChartModel.legend)

        val barDataSet = BarDataSet(entries, "Target")
        barDataSet.setDrawValues(true)
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 6f
        barDataSet.colors = colors

        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f

        barChart.apply {
            setDrawValueAboveBar(false)
            extraBottomOffset = 10f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
            xAxis.labelCount = entries.size
            xAxis.textSize = 7f
            setTouchEnabled(false)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            data = barData
            animateY(800)
            invalidate()
        }
    }

    private fun setupRecyclerView(listItems: ArrayList<String>) {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        adapter = BarChartAdapter(requireContext(), listItems, this)
        binding.recyclerView.adapter = adapter
    }

    private fun legendSetup(myList: ArrayList<MyLegend>) {
        val legend: Legend = binding.barChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
        legend.xEntrySpace = 25f
        legend.setCustom(
            myList.map { legend1 ->
                LegendEntry(
                    legend1.legendValue,
                    Legend.LegendForm.CIRCLE,
                    8f,
                    0f,
                    null,
                    Utils.parseColorSafely(legend1.legendColor)
                )
            }
        )
    }

    private fun handleAPIResponse(cardTypeId: String) {
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
                        val responseBody = it.data?.body()
                        val recyclerViewItems: ArrayList<String> = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel?.add(
                                Gson().fromJson(
                                    jsonArray,
                                    StackGraphModel::class.java
                                )
                            )
                            graphModel?.get(index)?.label.let { it1 ->
                                if (it1 != null) {
                                    recyclerViewItems.add(it1)
                                }
                            }
                        }

                        if (cardTypeId == "4") {
                            setupRecyclerView(recyclerViewItems)
                            binding.switchTD.visibility = View.GONE
                        } else {
                            binding.switchTD.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }

                        if (graphModel?.isNotEmpty() == true) graphModel?.get(0)?.let { it1 -> showBarChart(it1, binding.barChart, true) }
                    }
                }
            }
        }
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        graphModel?.get(position)?.let { showBarChart(it, binding.barChart, false) }
    }
}
