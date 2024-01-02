package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.adapter.HalfPieAdapter
import com.example.meezan360.databinding.FragmentProductWiseChartBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.TierGraphModel
import com.example.meezan360.model.footerGraph.data.TierChartDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HalfPieFragment(val kpiId: Int?, val tagName: String, val dataModel: DataModel) : Fragment(),
    OnItemClickListener {

    private lateinit var binding: FragmentProductWiseChartBinding
    private val myViewModel: DashboardViewModel by viewModel()
    private lateinit var adapter: BarChartAdapter
    private val graphModel: ArrayList<TierGraphModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductWiseChartBinding.inflate(inflater, container, false)
        binding.tvTitle.text = dataModel.cardTitle
        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), tagName, dataModel.cardId)
        }
        handleAPIResponse()

        return binding.root
    }

    private fun showPieChart(tierGraphModel: TierGraphModel) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()
        val listItems = ArrayList<TierChartDataModel>()
        tierGraphModel.data.forEachIndexed { _, tierChartDataModel ->
            tierChartDataModel.volumn?.let { PieEntry(it.toFloat(), tierChartDataModel.key) }
                ?.let { pieEntries.add(it) }
            colors.add(Color.parseColor(tierChartDataModel.color))
            listItems.add(
                TierChartDataModel(
                    key = tierChartDataModel.key,
                    value = tierChartDataModel.value,
                    color = tierChartDataModel.color
                )
            )
        }

        setupRecyclerView2(listItems)

        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)

        binding.pieChart.apply {
            isRotationEnabled = false
            description.isEnabled = false
            setDrawSliceText(false)
            rotationAngle = 180f
            maxAngle = 180f
            legend.isEnabled = false
            data = pieData
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

                    is ResponseModel.Idle -> {}

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        val responseBody = it.data?.body()
                        val recyclerViewItems: ArrayList<String> = arrayListOf()

                        responseBody?.asJsonArray?.forEachIndexed { index, _ ->
                            val jsonArray = responseBody.asJsonArray.get(index).toString()
                            graphModel.add(
                                Gson().fromJson(
                                    jsonArray,
                                    TierGraphModel::class.java
                                )
                            )
                            graphModel[index].label.let { it1 -> recyclerViewItems.add(it1) }
                        }

                        setupRecyclerView(recyclerViewItems)

                        showPieChart(graphModel[0])

                    }
                }
            }
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

    private fun setupRecyclerView2(listItems: ArrayList<TierChartDataModel>) {
        binding.recyclerView2.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.recyclerView2.adapter = HalfPieAdapter(requireContext(), listItems)
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
        showPieChart(graphModel[position])
    }
}
