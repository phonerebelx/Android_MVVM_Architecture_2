package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.databinding.FragmentTopBottomBranchesBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter


class TopBottomBranches : Fragment() {

    private lateinit var binding: FragmentTopBottomBranchesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopBottomBranchesBinding.inflate(layoutInflater)
        showBarChart()
        return binding.root
    }

    private fun showBarChart() {
        val valueList =
            listOf(
                -1700.0,
                -7500.0,
                -1017.0,
                -4500.0,
                -3500.0,
                1700.0,
                7500.0,
                1017.0,
                4500.0,
                3500.0,
            ) // Sample data, replace with your values
        val mValues = arrayOf(
            "0101 - PNSC",
            "0101 - North Nazimabad",
            "0101 - Shaikhupura",
            "0101 - Apr",
            "0101 - May",
            "0101 - PNSC",
            "0101 - North Nazimabad",
            "0101 - Shaikhupura",
            "0101 - Apr",
            "0101 - May"
        )

        val entries: ArrayList<BarEntry> = ArrayList()

        // Fit the data into a bar
        for (i in valueList.indices) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "Target")

        barDataSet.colors = listOf(
            Color.parseColor("#3A9F5E"),
            Color.parseColor("#DB4336"),
        )

        val totalSum = valueList.sum()
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
//                val percentage = (abs(value.toInt()) / totalSum) * 100
//                val formatted = "%.1f%%".format(percentage)
                return value.toInt().toString()
            }
        }

        barDataSet.setDrawValues(true)
        barDataSet.valueTextSize = 8f
        barDataSet.valueTextColor = Color.WHITE

        val mData = BarData(barDataSet)
        mData.barWidth = 0.7f
        mData.isHighlightEnabled = false

        //Display the axis on the left
        val xAxis = binding.horizontalBarChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(false)
        xAxis.labelCount = valueList.size

        xAxis.valueFormatter = object : IndexAxisValueFormatter(mValues) {
            override fun getFormattedValue(value: Float): String {
                return if (value >= 0 && value.toInt() < mValues.size) {
                    mValues[value.toInt()]
                } else {
                    ""
                }
            }
        }

        binding.horizontalBarChart.apply {
            axisLeft.axisMaximum =
                10000f //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
            axisLeft.axisMinimum = -10000f
            axisRight.axisMinimum = 10000f
            axisRight.axisMaximum = -10000f
            legend.isEnabled = false
            setDrawValueAboveBar(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
            description.isEnabled = false

            invalidate()
        }
    }


}