package com.example.meezan360.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meezan360.databinding.FragmentMomTargetVsAchievementBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class MoMTargetVsAchievementFragment : Fragment() {

    private lateinit var binding: FragmentMomTargetVsAchievementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMomTargetVsAchievementBinding.inflate(layoutInflater)
        showCombineChart()
        return binding.root
    }

    private fun showCombineChart() {

        //bar chart
        val valueList = listOf(
            20.0,
            45.0,
            15.0,
            25.0,
            35.0,
            25.0,
            35.0,
            35.0,
            25.0,
            38.0,
            30.0,
            38.0
        ) // Sample data

        val entries: ArrayList<BarEntry> = ArrayList()

        for (i in valueList.indices) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "Target")

        val startColor = Color.parseColor("#1F753E")
        val endColor = Color.parseColor("#3A9F5E")
        barDataSet.setGradientColor(startColor, endColor)

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f

        val labels = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "June",
            "July",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        ///

        //line chart
        val lineEntries = ArrayList<Entry>()
        lineEntries.add(Entry(-0.3f, 27f))
        lineEntries.add(Entry(0.3f, 27f))

        val lineEntries2 = ArrayList<Entry>()
        lineEntries2.add(Entry(0.7f, 52f))
        lineEntries2.add(Entry(1.3f, 52f))

        val lineEntries3 = ArrayList<Entry>()
        lineEntries3.add(Entry(1.7f, 22f))
        lineEntries3.add(Entry(2.2f, 22f))

        val mLineDataSet = LineDataSet(lineEntries, "Line Chart")
        val mLineDataSet2 = LineDataSet(lineEntries2, "")
        val mLineDataSet3 = LineDataSet(lineEntries3, "")

        lineChartCustomization(mLineDataSet)
        lineChartCustomization(mLineDataSet2)
        lineChartCustomization(mLineDataSet3)

        val lineData = LineData(mLineDataSet, mLineDataSet2, mLineDataSet3)

        val combineData = CombinedData()
        combineData.setData(barData)
        combineData.setData(lineData)

        val legend: Legend = binding.combineChart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.textColor = Color.parseColor("#676767")
        legend.xEntrySpace = 25f
        val l1 = LegendEntry("Target", Legend.LegendForm.SQUARE, 8f, 0f, null, Color.parseColor("#DB4336"))
        val l2 = LegendEntry("Achievement", Legend.LegendForm.DEFAULT, 8f, 0f, null, Color.parseColor("#3A9F5E"))
        legend.setCustom(arrayOf(l1, l2))

        binding.combineChart.apply {
            extraBottomOffset = 10f
            description.isEnabled = false
            xAxis.spaceMin = barData.barWidth / 2f //first bar to show properly
            xAxis.spaceMax = barData.barWidth / 2f //last bar to show properly
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = valueList.size
            xAxis.textSize = 7f
            xAxis.textColor = Color.parseColor("#676767")
            xAxis.setDrawGridLines(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = combineData
        }
    }

    private fun lineChartCustomization(mLineDataSet: LineDataSet) {

        mLineDataSet.lineWidth = 2.6f
        mLineDataSet.color = Color.parseColor("#E8544F")
        mLineDataSet.setDrawCircles(false)
        mLineDataSet.setDrawValues(false)
    }

}