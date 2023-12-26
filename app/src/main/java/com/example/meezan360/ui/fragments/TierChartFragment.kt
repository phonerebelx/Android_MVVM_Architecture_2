package com.example.meezan360.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.meezan360.databinding.FragmentTierWiseDepositBinding
import com.example.meezan360.model.dashboardByKpi.DataModel


class TierChartFragment(kpiId: Int?, tagName: String, dataModel: DataModel) : Fragment() {

    private lateinit var binding: FragmentTierWiseDepositBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTierWiseDepositBinding.inflate(layoutInflater)

        setupPyramidChart()

        return binding.root
    }

    private fun setupPyramidChart() {

        val pyramidChart = AnyChart.pyramid()

        val mData = listOf(
            ValueDataEntry("5 M and above", 100),
            ValueDataEntry("3 M - 5 M", 150),
            ValueDataEntry("1 M - 3 M", 150),
            ValueDataEntry("500 K - 1 M", 250),
            ValueDataEntry("100 K - 500 K", 250)
        )

        val colors: Array<String> = arrayOf(
            "#EE4E61",
            "#866EC0",
            "#14A1C1",
            "#03CC9D",
            "#F8CC38"
        )

        pyramidChart.apply {
            data(mData)
//            fill(colors)
//            fill("aquastyle")
            legend().enabled(true)
            legend().fontSize(12)
            legend().padding(0, 0, 10, 0)
//            title().padding(10, 0, 10, 0)
//            title().fontSize(18)
//            title().fontColor("#333")
            labels().format("{%Value}%")
            labels().fontColor("#ffffff")
            labels().fontSize(10)
            labels().position("inside")
            credits(false)
        }

        // Display the chart
        binding.anyChartView.setChart(pyramidChart)
    }

}