package com.example.meezan360.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.databinding.FragmentMonthlyReportBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class MonthlyReportFragment : Fragment() {

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
        val valueList = listOf(490.0, 410.0, 900.0, 200.0) // Sample data, replace with your values
        val valueList2 = listOf(590.0, 310.0, 400.0, 300.0) // Sample data, replace with your values
        val valueList3 = listOf(690.0, 410.0, 900.0, 200.0) // Sample data, replace with your values
        val valueList4 = listOf(590.0, 310.0, 400.0, 300.0) // Sample data, replace with your values

        val entries: ArrayList<BarEntry> = ArrayList()
        val entries2: ArrayList<BarEntry> = ArrayList()
        val entries3: ArrayList<BarEntry> = ArrayList()
        val entries4: ArrayList<BarEntry> = ArrayList()

        // Fit the data into a bar
        for (i in valueList.indices) {
            val barEntry =
                BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        // Fit the data into a bar
        for (i in valueList2.indices) {
            val barEntry =
                BarEntry(i.toFloat(), valueList2[i].toFloat())
            entries2.add(barEntry)
        }

        // Fit the data into a bar
        for (i in valueList2.indices) {
            val barEntry =
                BarEntry(i.toFloat(), valueList3[i].toFloat())
            entries3.add(barEntry)
        }

        // Fit the data into a bar
        for (i in valueList2.indices) {
            val barEntry =
                BarEntry(i.toFloat(), valueList4[i].toFloat())
            entries4.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "Target")
        val barDataSet2 = BarDataSet(entries2, "Target 2")
        val barDataSet3 = BarDataSet(entries3, "Target 3")
        val barDataSet4 = BarDataSet(entries4, "Target 4")

        val mColor: List<Int> =
            listOf(
                Color.parseColor("#F9C000"),
                Color.parseColor("#A087DA"),
                Color.parseColor("#20773F"),
                Color.parseColor("#1548AA"),
            )

        barDataSet.setDrawValues(true)
        barDataSet.valueTextColor = Color.parseColor("#676767")
        barDataSet.valueTextSize = 6f
        barDataSet.colors = mColor

        barDataSet2.setDrawValues(true)
        barDataSet2.valueTextColor = Color.parseColor("#676767")
        barDataSet2.valueTextSize = 6f
        barDataSet2.colors = mColor

        barDataSet3.setDrawValues(true)
        barDataSet3.valueTextColor = Color.parseColor("#676767")
        barDataSet3.valueTextSize = 6f
        barDataSet3.colors = mColor

        barDataSet4.setDrawValues(true)
        barDataSet4.valueTextColor = Color.parseColor("#676767")
        barDataSet4.valueTextSize = 6f
        barDataSet4.colors = mColor

        val barData = BarData(barDataSet, barDataSet2, barDataSet3, barDataSet4)
        barData.barWidth = 0.1f

        val labels = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
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
            xAxis.textColor = Color.parseColor("#676767")
            xAxis.labelCount = valueList.size
            xAxis.textSize = 7f
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            invalidate()
        }

    }

}