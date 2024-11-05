package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentOnOffBranchesBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.StackGraphModel
import com.example.meezan360.model.footerGraph.data.MyLegend
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class StackChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    BaseDockFragment(), OnItemClickListener {

    private lateinit var binding: FragmentOnOffBranchesBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private var graphModel: ArrayList<StackGraphModel>? = null
    private lateinit var adapter: BarChartAdapter
    lateinit var graphArrayWithoutTd: ArrayList<StackGraphModel?>
    lateinit var graphArrayWithTd: ArrayList<StackGraphModel?>
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

            Log.d("graphArrayWithTd",graphArrayWithTd.toString())
            if (graphArrayWithoutTd.isNotEmpty()){
                if (isChecked){
                    graphArrayWithTd[0]?.let { showBarChart(it, binding.barChart, isChecked) }
                }else  {
                    graphArrayWithoutTd[0]?.let { graph -> graph.stackChartData.removeIf { it.key == "TD" } }
                    graphArrayWithoutTd[0]?.let { showBarChart(it, binding.barChart, isChecked) }
                }

            }


        }

        return binding.root
    }

//    private fun showBarChart(
//        stackChartModel: StackGraphModel,
//        barChart: BarChart,
//        switchTD: Boolean
//    ) {
//
//        val entries: ArrayList<BarEntry> = arrayListOf()
//        val colors: ArrayList<Int> = arrayListOf()
//        val labels = ArrayList<String>()
//
//        stackChartModel.stackChartData.forEachIndexed { _, stackChartDataModel ->
//            if (!(stackChartDataModel.key.equals("term", true) && !switchTD)) {
//                val barEntry = BarEntry(
//                    entries.size.toFloat(),
//                    floatArrayOf(
//                        stackChartDataModel.value1.toFloat(),
//                        stackChartDataModel.value2.toFloat(),
//                        stackChartDataModel.value3.toFloat()
//                    )
//                )
//                entries.add(barEntry)
//                colors.add(Utils.parseColorSafely(stackChartDataModel.value1Color))
//                colors.add(Utils.parseColorSafely(stackChartDataModel.value2Color))
//                colors.add(Utils.parseColorSafely(stackChartDataModel.value3Color))
//                labels.add(stackChartDataModel.key)
//            }
//        }
//
//        legendSetup(stackChartModel.legend)
//
//        val barDataSet = BarDataSet(entries, "Target")
//        barDataSet.setDrawValues(true)
//        barDataSet.valueTextColor = Color.WHITE
//        barDataSet.valueTextSize = 6f
//        barDataSet.colors = colors
//
//        val barData = BarData(barDataSet)
//        barData.barWidth = 0.3f
//
//        barChart.apply {
//            setDrawValueAboveBar(false)
//            extraBottomOffset = 10f
//            axisLeft.isEnabled = false
//            axisRight.isEnabled = false
//            description.isEnabled = false
//            xAxis.setDrawGridLines(false)
//            xAxis.setDrawAxisLine(false)
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
//            xAxis.labelCount = labels.size
//            xAxis.textSize = 7f
//            setTouchEnabled(false)
//            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//            data = barData
//            animateY(800)
//            invalidate()
//            if (dataModel.isVerticalLegend == "1"){
//                xAxis.labelRotationAngle = -90f
//            }
//        }
//    }


    private fun showBarChart(
        stackChartModel: StackGraphModel,
        barChart: BarChart,
        switchTD: Boolean
    ) {

        val entries: ArrayList<BarEntry> = arrayListOf()
        val colors: ArrayList<Int> = arrayListOf()
        val labels = ArrayList<String>()

        Log.d( "labelsShowBarChart: ",stackChartModel.toString() )


        stackChartModel.stackChartData.forEachIndexed { _, stackChartDataModel ->
            if (!(stackChartDataModel.key.equals("term", true) && !switchTD)) {
                val barEntry = BarEntry(
                    entries.size.toFloat(),
                    floatArrayOf(
                        stackChartDataModel.value1.toFloat(),
                        stackChartDataModel.value2.toFloat(),
                        stackChartDataModel.value3.toFloat()
                    )
                )
                entries.add(barEntry)
                colors.add(Utils.parseColorSafely(stackChartDataModel.value1Color))
                colors.add(Utils.parseColorSafely(stackChartDataModel.value2Color))
                colors.add(Utils.parseColorSafely(stackChartDataModel.value3Color))
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
        if (entries.size > 5){
            barData.barWidth = 0.7f
        }else if (entries.size > 4){
            barData.barWidth = 0.5f
        }else{
            barData.barWidth = 0.6f
        }

        // Customize value formatter to hide 0 values
        barData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value == 0f) "" else value.toInt().toString()
            }
        })

        barChart.apply {
            setDrawValueAboveBar(false)
            setPadding(0, 0, 0, 0)
            extraRightOffset = 0f
            extraLeftOffset = 0f
            extraTopOffset = 0f
            extraBottomOffset = 10f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
            xAxis.labelCount = labels.size
            xAxis.textSize = 7f


            setTouchEnabled(false)
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
            data = barData
            animateY(800)
            invalidate()
            if (dataModel.isVerticalLegend == "1"){
                xAxis.labelRotationAngle = -90f
            }
        }
    }


    private fun setupRecyclerView(listItems: ArrayList<String>) {
        if (listItems.size == 1){
            binding.recyclerView.visibility = View.GONE
        }
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

                        if (responseBody?.asJsonArray?.isEmpty == true){
                            binding.barChart.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }
                        val recyclerViewItems: ArrayList<String> = arrayListOf()
                        graphArrayWithTd = arrayListOf()
                        graphArrayWithoutTd = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel?.add(
                                Gson().fromJson(
                                    jsonArray,
                                    StackGraphModel::class.java
                                )
                            )
                            graphArrayWithTd.add(
                                Gson().fromJson(
                                    jsonArray,
                                    StackGraphModel::class.java
                                )
                            )

                            graphArrayWithoutTd.add(
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

        if (graphModel?.get(position)?.stackChartData?.isEmpty() == true){
            binding.barChart.visibility = View.GONE
            binding.tvView.visibility = View.VISIBLE
        }else{
            binding.barChart.visibility =View.VISIBLE
            binding.tvView.visibility = View.GONE
            graphModel?.get(position)?.let { showBarChart(it, binding.barChart, false) }
        }


    }



    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.switchTD.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.switchTD.visibility = View.GONE

    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.switchTD.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.switchTD.visibility = View.VISIBLE
    }

}
