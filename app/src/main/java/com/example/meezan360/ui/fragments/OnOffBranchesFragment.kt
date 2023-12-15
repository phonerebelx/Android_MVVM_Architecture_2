package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.databinding.FragmentOnOffBranchesBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.model.GradientColor


class OnOffBranchesFragment : Fragment() {

    private lateinit var binding: FragmentOnOffBranchesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOnOffBranchesBinding.inflate(layoutInflater)

        showBarChart()
        return binding.root
    }

    private fun showBarChart() {
        val valueList = listOf(490.0, 410.0, 900.0, 200.0) // Sample data, replace with your values
        val valueList2 = listOf(940.0, 940.0, 940.0, 940.0) // Sample data, replace with your values

        val entries: ArrayList<BarEntry> = ArrayList()


        // Fit the data into a bar
        for (i in valueList.indices) {
            val barEntry =
                BarEntry(i.toFloat(), floatArrayOf(valueList[i].toFloat(), valueList2[i].toFloat()))
            entries.add(barEntry)
        }


        val barDataSet = BarDataSet(entries, "Target")

        val startColor = Color.parseColor("#EF4949")
        val endColor = Color.parseColor("#DB4336")

        val startColor2 = Color.parseColor("#856BC1")
        val endColor2 = Color.parseColor("#4D338B")

        val gradientColor: List<GradientColor> =
            listOf(GradientColor(startColor2, endColor2), GradientColor(endColor, startColor))

        barDataSet.setDrawValues(true)
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 6f

        barDataSet.setGradientColor(startColor, endColor)
        barDataSet.gradientColors = gradientColor


        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f

        val legend: Legend = binding.barChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry("ON Branches", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#6348A0"))
        val l2 = LegendEntry("OFF Branches", Legend.LegendForm.CIRCLE, 8f, 0f, null, Color.parseColor("#E8544F"))
        legend.setCustom(arrayOf(l1, l2))

        val labels = arrayOf(
            "CA",
            "SA",
            "CASA",
            "Total",
        )

        binding.barChart.apply {
            setDrawValueAboveBar(false)
            extraBottomOffset = 10f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.parseColor("#676767")
            xAxis.labelCount = valueList.size
            xAxis.textSize = 7f
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            data = barData

            invalidate()
        }

    }

}