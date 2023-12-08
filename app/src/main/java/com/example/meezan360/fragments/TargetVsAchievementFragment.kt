package com.example.meezan360.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentTargetVsAchievementBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter


class TargetVsAchievementFragment : Fragment() {

    private lateinit var binding: FragmentTargetVsAchievementBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTargetVsAchievementBinding.inflate(layoutInflater)
        showBarChart()
        showPieChart()
        return binding.root
    }

    private fun showBarChart() {
        val values = arrayOf("Saving", "Current", "CASA", "Term Deposit")
        val valueList = listOf(100.0, 150.0, 200.0, 120.0) // Sample data, replace with your values

        val entries: ArrayList<BarEntry> = ArrayList()

        // Fit the data into a bar
        for (i in valueList.indices) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "Target")

        barDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.purple_light),
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.golden),
            ContextCompat.getColor(requireContext(), R.color.grey),
        )

        val totalSum = valueList.sum()
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val percentage = (value / totalSum) * 100
                return "%.1f%%".format(percentage)
            }
        }

        barDataSet.setDrawValues(true)
        barDataSet.valueTextSize = 12f
        barDataSet.valueTextColor = Color.BLACK

        val mData = BarData(barDataSet)
        mData.barWidth = 0.5f
        mData.isHighlightEnabled = false


        //Display the axis on the left (contains the labels 1*, 2* and so on)
        val xAxis = binding.horizontalBarChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(false)
        xAxis.labelCount = 4

        //Now add the labels to be added on the vertical axis
        val mValues = arrayOf("Saving", "Current", "CASA", "Term Deposit")
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
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
            description.isEnabled = false

            invalidate()
        }
    }

    private fun showPieChart() {

        val pieEntryValueCA = 75f
        // Input data and fit data into pie chart entry
        val pieEntries = mutableListOf<PieEntry>()

        pieEntries.add(PieEntry(pieEntryValueCA))
        pieEntries.add(PieEntry(100 - pieEntryValueCA))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#1E90E2"))
        colors.add(Color.parseColor("#FAFAFA"))

        // Collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, "")
        // Setting text size of the value (hide text values)
        pieDataSet.valueTextSize = 0f
        // Providing color list for coloring different entries
        pieDataSet.colors = colors
        pieDataSet.selectionShift = 0f


        binding.pieChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            centerText = "75%"
            extraLeftOffset = 25f
            setHoleColor(Color.parseColor("#E0E0E0"))
            setCenterTextSize(12f)
            setCenterTextColor(Color.parseColor("#7B7878"))
            data = PieData(pieDataSet)
            invalidate()
        }


    }


}