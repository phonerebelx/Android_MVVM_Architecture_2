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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
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
        val valueList =
            listOf(1700.0, 7500.0, 1017.0, 4500.0) // Sample data, replace with your values

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
                val formatted = "%.1f%%".format(percentage)
                return "${value.toInt()}($formatted)"
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
        xAxis.labelCount = 4

        val labels = setupLegend()

        binding.horizontalBarChart.apply {
            axisLeft.axisMaximum =
                10000f //must define axis maximum and minimum to show text labels inside horizontal bars (this condition only applicable for horizontal bars)
            axisLeft.axisMinimum = 0f
            setDrawValueAboveBar(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
            description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            invalidate()
        }
    }

    private fun setupLegend(): Array<String> {
        val legend: Legend = binding.horizontalBarChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Target", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F")
        )
        legend.setCustom(arrayOf(l1))

        return arrayOf("Term Deposit", "CASA", "Saving", "Current")
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