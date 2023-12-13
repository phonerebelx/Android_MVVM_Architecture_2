package com.example.meezan360.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.databinding.FragmentCustomerDepositBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class CustomerDepositFragment : Fragment() {

    private lateinit var binding: FragmentCustomerDepositBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentCustomerDepositBinding.inflate(layoutInflater)
        showBarChart()
        return binding.root
    }

    private fun showBarChart() {
        val valueList =
            listOf(
                -1000.0,
                -700.0,
                -500.0,
                -200.0,
                -1000.0
            )

        val entries: ArrayList<BarEntry> = ArrayList()

        // Fit the data into a bar
        for (i in valueList.indices) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "Target")

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#BBBBBB"))
        colors.add(Color.parseColor("#A087DA"))
        colors.add(Color.parseColor("#EC6565"))
        colors.add(Color.parseColor("#F9C000"))
        colors.add(Color.parseColor("#BBBBBB"))
        barDataSet.colors = colors

        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f

        val labels = setupLegend()

        binding.barChart.apply {
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

            data = barData

            invalidate()
        }

    }

    private fun setupLegend(): Array<String> {
        val legend: Legend = binding.barChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry(
            "Increase", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#6348A0")
        )
        val l2 = LegendEntry(
            "Decrease", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F")
        )
        val l3 = LegendEntry(
            "Net Off", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#FFC400")
        )
        legend.setCustom(arrayOf(l1, l2, l3))

        return arrayOf(
            "Last Day",
            "Increase",
            "Decrease",
            "Net Off",
            "Current Day",
        )
    }


}