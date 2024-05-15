package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentMonthlyReportBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class CombineBarChartFragment : Fragment() {

    private lateinit var binding: FragmentMonthlyReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthlyReportBinding.inflate(layoutInflater)
        showBarChart()
        return binding.root
    }

    private fun showBarChart() {
        val valueLists = listOf(
            listOf(490.0, 410.0, 900.0, 200.0),
            listOf(590.0, 310.0, 400.0, 300.0),
            listOf(690.0, 410.0, 900.0, 200.0),
            listOf(590.0, 310.0, 400.0, 300.0)
        )

        val entriesList: List<ArrayList<BarEntry>> = valueLists.map { values ->
            val entries: ArrayList<BarEntry> = ArrayList()
            values.forEachIndexed { index, value ->
                entries.add(BarEntry(index.toFloat(), value.toFloat()))
            }
            entries
        }

        val barDataSets = entriesList.mapIndexed { index, entries ->
            val barDataSet = BarDataSet(entries, "Target ${index + 1}")
            barDataSet.setDrawValues(true)
            barDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.grey2)
            barDataSet.valueTextSize = 6f
            barDataSet.colors = listOf(
                Color.parseColor("#F9C000"),
                Color.parseColor("#A087DA"),
                Color.parseColor("#20773F"),
                Color.parseColor("#1548AA")
            )
            barDataSet
        }

        val barData = BarData(barDataSets)
        barData.barWidth = 0.1f

        val labels = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr"
        )

        binding.barChart.apply {
            data = barData
            groupBars(0f, 0.2f, 0.07f)
            legend.isEnabled = false
            extraBottomOffset = 10f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.grey2)
            xAxis.labelCount = valueLists.first().size
            xAxis.textSize = 7f
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.setDrawAxisLine(true)
            invalidate()
        }

    }

}