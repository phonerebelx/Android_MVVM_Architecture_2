package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.LineChartAdapter
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentDepositTrendBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.Utils
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LineChartFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) :
    BaseDockFragment(), OnItemClickListener {
    private lateinit var binding: FragmentDepositTrendBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private lateinit var adapter: LineChartAdapter
    private var graphModel: ArrayList<HorizontalGraphModel> = arrayListOf()
    private val positionsList = ArrayList<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositTrendBinding.inflate(layoutInflater)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()
        return binding.root
    }

    private fun drawLineChart(
        tierGraphModel: ArrayList<HorizontalGraphModel>,
        positionsList: ArrayList<Int>
    ) {

        if (tierGraphModel.isEmpty() || positionsList.isEmpty()){
            return
        }

        val listOfDataSet: MutableList<LineDataSet> = mutableListOf()
        var labels = ArrayList<String>()
        for (i in positionsList) {
            val lineEntries = ArrayList<Entry>()
            tierGraphModel[i].barChartModel.forEachIndexed { index, dataModel ->
                labels.add(dataModel.key)

                lineEntries.add(Entry(index.toFloat(), dataModel.value))
            }

            val labelsSet = linkedSetOf<String>().apply { addAll(labels) }
            labels = ArrayList(labelsSet)
//            for (dataModel in tierGraphModel[i].barChartModel) {
//                lineEntries.add(Entry(dataModel.key.toFloat(), dataModel.value))
//            }

            val lineDataSet = LineDataSet(lineEntries, "")
            customizationLine(lineDataSet, Utils.parseColorSafely(tierGraphModel[i].color))
            listOfDataSet.add(lineDataSet)
        }

        val lineData = LineData(listOfDataSet as List<ILineDataSet>?)

        binding.lineChart.apply {
            description.isEnabled = false
            setDrawMarkers(false)
            setPadding(0, 0, 0, 0)
            extraRightOffset = 0f
            extraLeftOffset = 0f
            extraTopOffset = 0f
            extraBottomOffset = 15f
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1.0f
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelCount = labels.count()
            data = lineData
            animateXY(1000, 1000)
            if (dataModel.isVerticalLegend == "1"){
                xAxis.labelRotationAngle = -90f
            }
            val paintShadow: Paint = renderer.paintRender
            paintShadow.setShadowLayer(5F, 2F, 2F, Color.GRAY);

            invalidate()
        }

    }

    private fun customizationLine(lineDataSet: LineDataSet, color: Int) {
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.lineWidth = 2f
        lineDataSet.color = color
        lineDataSet.setCircleColor(color)
        lineDataSet.circleRadius = 5f
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.valueTextSize = 12f
        lineDataSet.valueTextColor = Color.DKGRAY
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
                        if (responseBody?.asJsonArray?.isEmpty == true){
                            binding.lineChart.visibility = View.GONE
                            binding.tvView.visibility = View.VISIBLE
                        }
                        val recyclerViewItems: ArrayList<String> = arrayListOf()
                        graphModel = arrayListOf()
                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()

                            graphModel.add(
                                Gson().fromJson(
                                    jsonArray,
                                    HorizontalGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it1 ->
                                if (it1 != null) {
                                    recyclerViewItems.add(it1)
                                }
                            }
                            positionsList.add(index)
                        }

                        setupRecyclerView(graphModel)

                        //for first line to show
                        drawLineChart(graphModel, positionsList)

                    }
                }
            }
        }

    }

    private fun setupRecyclerView(listItems: ArrayList<HorizontalGraphModel>) {
        if (listItems.size == 1) binding.recyclerView.visibility = View.GONE
        binding.recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        adapter = LineChartAdapter(requireContext(), listItems, this)
        binding.recyclerView.adapter = adapter


    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {

        if (checked == true) {
            positionsList.add(position)
        } else {
            positionsList.removeAll {
                it == position
            }
        }
        drawLineChart(graphModel, positionsList)
    }

    private fun showProgressIndicator() {
        binding.rlLoader.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.lineChart.visibility = View.GONE


    }


    private fun hideProgressIndicator() {
        binding.rlLoader.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.lineChart.visibility = View.VISIBLE

    }

}