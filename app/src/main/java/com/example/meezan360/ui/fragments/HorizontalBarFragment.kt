package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentTopBottomBranchesBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.InvertedHorizontalGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class HorizontalBarFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    BaseDockFragment(), OnItemClickListener {

    private lateinit var binding: FragmentTopBottomBranchesBinding
    private lateinit var adapter: BarChartAdapter
    private val myViewModel: DashboardViewModel by viewModel()
    private val graphModel: ArrayList<InvertedHorizontalGraphModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopBottomBranchesBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()

        return binding.root
    }

//    private fun showBarChart(
//        chartModelTop: ArrayList<HorizontalBarChartDataModel>,
//        chartModelBottom: ArrayList<HorizontalBarChartDataModel>,
//        horizontalBarChart: HorizontalBarChart
//    ) {
//        val entries: ArrayList<BarEntry> = ArrayList()
//        val myColors = ArrayList<Int>()
//        val labels: ArrayList<String> = arrayListOf()
//
//        var lastIndex = 0f
//        chartModelTop.forEachIndexed { index, dataModel ->
//            entries.add(BarEntry(index.toFloat(), dataModel.value))
//            myColors.add(Utils.parseColorSafely(dataModel.valueColor))
//            labels.add(dataModel.key)
//            lastIndex = index.toFloat()
//        }
//        var updatedIndex = lastIndex + 1
//        for (dataModel in chartModelBottom) {
//            entries.add(BarEntry(updatedIndex, dataModel.value))
//            myColors.add(Utils.parseColorSafely(dataModel.valueColor))
//            labels.add(dataModel.key)
//            updatedIndex++
//        }
//        Log.d("updatedIndex entries",entries.toString())
//        Log.d("updatedIndex labels",labels.toString())
//        val barDataSet = BarDataSet(entries, "Target")
//
//        barDataSet.colors = myColors
//
////        val totalSum = valueList.sum()
//        barDataSet.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
////                val percentage = (abs(value.toInt()) / totalSum) * 100
////                val formatted = "%.1f%%".format(percentage)
//                return value.toInt().toString()
//            }
//        }
//
//        barDataSet.setDrawValues(true)
//        barDataSet.valueTextSize = 8f
//        barDataSet.valueTextColor = Color.WHITE
//
//        val mData = BarData(barDataSet)
//        mData.barWidth = 0.7f
//        mData.isHighlightEnabled = false
//
//        //Display the axis on the left
//        val xAxis = horizontalBarChart.xAxis
//        xAxis.spaceMin = mData.barWidth / 2f // First bar to show properly
//        xAxis.spaceMax = mData.barWidth / 2f // Last bar to show properly
//        xAxis.setDrawGridLines(false)
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawAxisLine(false)
//        xAxis.labelCount = labels.size
//
//        xAxis.valueFormatter = object : IndexAxisValueFormatter(labels) {
//            override fun getFormattedValue(value: Float): String {
//                return if (value >= 0 && value.toInt() < labels.size) {
//                    labels[value.toInt()]
//                } else {
//                    ""
//                }
//            }
//        }
//
//        val maxValue = entries.maxByOrNull { it.y }
//        val minValue = entries.minByOrNull { it.y }
//
//        horizontalBarChart.apply {
//            axisLeft.axisMaximum = maxValue?.y!!
//            //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
//            axisLeft.axisMinimum = minValue?.y!!
//            axisRight.axisMinimum = minValue.y
//            axisRight.axisMaximum = maxValue.y
//            legend.isEnabled = false
//            setDrawValueAboveBar(false)
//            axisLeft.isEnabled = false
//            axisRight.isEnabled = false
//            data = mData
//            description.isEnabled = false
//            invalidate()
//        }
//    }


    private fun showBarChart(
        chartModelTop: ArrayList<HorizontalBarChartDataModel>,
        chartModelBottom: ArrayList<HorizontalBarChartDataModel>,
        horizontalBarChart: HorizontalBarChart
    ) {
        val topEntries: ArrayList<BarEntry> = ArrayList()
        val bottomEntries: ArrayList<BarEntry> = ArrayList()
        val topColors = ArrayList<Int>()
        val bottomColors = ArrayList<Int>()
        val labels: ArrayList<String> = arrayListOf()



        for (index in chartModelTop.size - 1 downTo 0) {
            val barChartModel = chartModelTop[index]

            topEntries.add(BarEntry(((chartModelTop.size - 1) - index).toFloat(), barChartModel.value))
            topColors.add(Utils.parseColorSafely(barChartModel.valueColor))
            labels.add(barChartModel.key)
        }


        val lastIndex = topEntries.size.toFloat()
        var updatedIndex = lastIndex
        for (index in chartModelBottom.size - 1 downTo 0) {
            val barChartModel = chartModelBottom[index]
            bottomEntries.add(BarEntry(updatedIndex, barChartModel.value))
            bottomColors.add(Utils.parseColorSafely(barChartModel.valueColor))
            labels.add(barChartModel.key)
            updatedIndex++
        }



        val topBarDataSet = BarDataSet(topEntries, "Top Bars")
        val bottomBarDataSet = BarDataSet(bottomEntries, "Bottom Bars")

        topBarDataSet.colors = topColors
        bottomBarDataSet.valueTextColor = Color.WHITE
        bottomBarDataSet.colors = bottomColors

        val mData = BarData(topBarDataSet, bottomBarDataSet)

        //Display the axis on the left
        val xAxis = horizontalBarChart.xAxis
        xAxis.spaceMin = mData.barWidth / 2f
        xAxis.spaceMax = mData.barWidth / 2f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(true)
        xAxis.labelCount = labels.size

        xAxis.valueFormatter = object : IndexAxisValueFormatter(labels) {
            override fun getFormattedValue(value: Float): String {
                return if (value >= 0 && value.toInt() < labels.size) {
                    labels[value.toInt()]
                } else {
                    ""
                }
            }
        }

        val maxValue = (topEntries + bottomEntries).maxByOrNull { it.y }
        val minValue = (topEntries + bottomEntries).minByOrNull { it.y }

        horizontalBarChart.apply {
            setPadding(0, 0, 0, 0)
            extraRightOffset = 0f
            extraLeftOffset = 0f
            extraTopOffset = 0f
            extraBottomOffset = 10f
            axisLeft.axisMaximum = maxValue?.y ?: 0f
            //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
            axisLeft.axisMinimum = minValue?.y?: 0f
            if (minValue != null) {
                axisRight.axisMinimum = minValue.y
            }
            if (maxValue != null) {
                axisRight.axisMaximum = maxValue.y
            }
            legend.isEnabled = false
            setDrawValueAboveBar(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
            description.isEnabled = false
            animateXY(1000, 800)
            invalidate()
        }
    }



    private fun setupRecyclerView(listItems: ArrayList<String>) {
        if (listItems.size == 1) {
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

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.footerGraph.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        hideProgressIndicator()
                        (requireActivity() as DockActivity).handleErrorResponse(it)

                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                        showProgressIndicator()
                    }

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
                        val responseBody = it.data?.body()

                        if (responseBody?.asJsonArray?.isEmpty == true){
                            binding.horizontalBarChart.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }
                        val listItems: ArrayList<String> = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel.add(
                                Gson().fromJson(
                                    jsonArray,
                                    InvertedHorizontalGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it2 -> listItems.add(it2) }
                        }
                        setupRecyclerView(listItems)

                        if (graphModel.isNotEmpty()) showBarChart(
                            graphModel[0].bottom,
                            graphModel[0].top,
                            binding.horizontalBarChart
                        )
                    }
                }
            }
        }
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {

        if (graphModel[position].bottom.isEmpty() && graphModel[position].top.isEmpty()){
            binding.horizontalBarChart.visibility = View.GONE
            binding.tvView.visibility = View.VISIBLE
        }else{
            binding.horizontalBarChart.visibility =View.VISIBLE
            binding.tvView.visibility = View.GONE
            showBarChart(
                graphModel[position].bottom,
                graphModel[position].top,
                binding.horizontalBarChart
            )
        }

    }

    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.horizontalBarChart.visibility = View.GONE


    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.horizontalBarChart.visibility = View.VISIBLE

    }
}