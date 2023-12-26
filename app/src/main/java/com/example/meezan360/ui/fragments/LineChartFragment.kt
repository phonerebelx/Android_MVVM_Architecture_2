package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meezan360.databinding.FragmentDepositTrendBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class LineChartFragment(kpiId: Int?, tagName: String, dataModel: DataModel) : Fragment() {
    private lateinit var binding: FragmentDepositTrendBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositTrendBinding.inflate(layoutInflater)
        drawLineChart()
        return binding.root
    }

    private fun drawLineChart() {

        val lineEntries = listOf(
            Entry(2016F, 0F),
            Entry(2017F, 25000F),
            Entry(2018F, 0F),
            Entry(2019F, 27000F),
            Entry(2020F, 28000F),
            Entry(2021F, 35000F),
            Entry(2022F, 40000F)
        )
        val lineEntries2 = listOf(
            Entry(2016F, 15000F),
            Entry(2017F, 27000F),
            Entry(2018F, 7F),
            Entry(2019F, 22000F),
            Entry(2020F, 0F),
            Entry(2021F, 30000F),
            Entry(2022F, 34000F)
        )

        val lineDataSet = LineDataSet(lineEntries, "")
        val lineDataSet2 = LineDataSet(lineEntries2, "")

        customizationLine(lineDataSet, Color.parseColor("#755AB2"))
        customizationLine(lineDataSet2, Color.parseColor("#FCD243"))

        val lineData = LineData(listOf(lineDataSet, lineDataSet2))

        binding.lineChart.apply {
            description.isEnabled = false
            setDrawMarkers(false)
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1.0f
            xAxis.labelCount = lineDataSet.entryCount
            data = lineData
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


}