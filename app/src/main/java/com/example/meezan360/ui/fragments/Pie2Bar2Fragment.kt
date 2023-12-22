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
import com.example.meezan360.databinding.FragmentDepositCompositionBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.footerGraph.Pie2Bar2Model
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel


class Pie2Bar2Fragment(private var kpiId: Int?, private var dataModel: DataModel) : Fragment() {

    private lateinit var binding: FragmentDepositCompositionBinding

    private val myViewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDepositCompositionBinding.inflate(layoutInflater)

        myViewModel.viewModelScope.launch {
            myViewModel.getFooterGraphs(kpiId.toString(), "pe_deposit", dataModel.cardId)
        }

        handleAPIResponse()

        showPieChart()


        return binding.root
    }

    private fun showPieChart() {

        val pieEntryValueCA = 75f
        var pieEntryValueCASA = 75f
        // Input data and fit data into pie chart entry
        val pieEntries = mutableListOf<PieEntry>()
        pieEntries.add(PieEntry(pieEntryValueCA))
        pieEntries.add(PieEntry(100 - pieEntryValueCA))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#856BC1"))
        colors.add(Color.parseColor("#FAFAFA"))

        // Collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, "")
        // Setting text size of the value (hide text values)
        pieDataSet.valueTextSize = 0f
        // Providing color list for coloring different entries
        pieDataSet.colors = colors
        pieDataSet.selectionShift = 0f

        setLegends(binding.pieChartCA, R.color.purple_light, Legend.LegendHorizontalAlignment.RIGHT)
        setLegends(binding.pieChartCASA, R.color.green, Legend.LegendHorizontalAlignment.LEFT)

        binding.pieChartCA.apply {
            description.text = "CA"
            description.xOffset = -70f
            description.yOffset = 30f
            description.textSize = 12f
            centerText = "75%"
            extraRightOffset = 70f
            setHoleColor(Color.parseColor("#E0E0E0"))
            setCenterTextSize(20f)
            setCenterTextColor(Color.parseColor("#7B7878"))
            setTouchEnabled(false) //to stop rotation
            data = PieData(pieDataSet)
            invalidate()
        }

        val colors2: ArrayList<Int> = ArrayList()
        colors2.add(Color.parseColor("#1F753E"))
        colors2.add(Color.parseColor("#FAFAFA"))

        val pieDataSet2 = PieDataSet(pieEntries, "")
        pieDataSet2.colors = colors2
        pieDataSet2.valueTextSize = 0f
        pieDataSet2.selectionShift = 0f

        binding.pieChartCASA.apply {
            description.text = "CASA"
            description.xOffset = 290f
            description.yOffset = 30f
            description.textSize = 12f
            centerText = "75%"
            extraLeftOffset = 70f
            setHoleColor(Color.parseColor("#E0E0E0"))
            setCenterTextSize(20f)
            setCenterTextColor(Color.parseColor("#7B7878"))
            setTouchEnabled(false) //to stop rotation
            data = PieData(pieDataSet2)
            invalidate()
        }

    }

    private fun setLegends(
        pieChart: PieChart,
        mColorRes: Int,
        alignment: Legend.LegendHorizontalAlignment,
    ) {
        val legend: Legend = pieChart.legend

        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = alignment
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.yEntrySpace = 5f
        legend.xOffset = 10f

        val legendColor = ContextCompat.getColor(requireContext(), mColorRes)

        val l1 = LegendEntry("90%", Legend.LegendForm.LINE, 90f, 15f, null, legendColor)
        val l2 = LegendEntry("60%", Legend.LegendForm.LINE, 60f, 15f, null, legendColor)
        val l3 = LegendEntry("80%", Legend.LegendForm.LINE, 80f, 15f, null, legendColor)

        legend.setCustom(arrayOf(l1, l2, l3))
        legend.isEnabled = true

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

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}
//                        Toast.makeText(
//                        context,
//                        "Loading..",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    is ResponseModel.Success -> {

                        val responseBody: String = it.data?.body().toString()
                        val json = JSONObject(responseBody)
                        val graph1 = json.get("graph1")
                        val responseObject: Pie2Bar2Model = Gson().fromJson(
                            json.optJSONObject("graph1")?.toString(),
                            Pie2Bar2Model::class.java
                        )

                    }
                }
            }
        }

    }

}