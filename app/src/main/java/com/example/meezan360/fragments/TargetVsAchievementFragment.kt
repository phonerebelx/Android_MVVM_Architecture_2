package com.example.meezan360.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meezan360.databinding.FragmentTargetVsAchievementBinding
import com.github.mikephil.charting.data.*


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
        val valueList = ArrayList<Double>()
        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "Target"




        //input data
        for (i in 0..5) {
            valueList.add(i * 100.1)
        }

        //fit the data into a bar
        for (i in 0 until valueList.size) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }


        val barDataSet = BarDataSet(entries, title)
        val mData = BarData(barDataSet)

//        val values = arrayOf("Current", "Saving", "CASA", "Term Deposit")

        binding.horizontalBarChart.apply {
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            data = mData
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
            setHoleColor(Color.parseColor("#E0E0E0"))
            setCenterTextSize(12f)
            setCenterTextColor(Color.parseColor("#7B7878"))
            data = PieData(pieDataSet)
            invalidate()
        }


    }


}