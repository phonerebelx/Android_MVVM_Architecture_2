package com.example.meezan360.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meezan360.databinding.FragmentProductWiseChartBinding
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class HalfPieFragment(kpiId: Int?, dataModel: DataModel) : Fragment() {

    private lateinit var binding: FragmentProductWiseChartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductWiseChartBinding.inflate(layoutInflater)
        showPieChart()
        return binding.root
    }

    private fun showPieChart() {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = "type"

        //initializing data
        val typeAmountMap: MutableMap<String, Int> = HashMap()
        typeAmountMap["CreditCard"] = 40
        typeAmountMap["DebitCard"] = 40
        typeAmountMap["CarIjarah"] = 40
        typeAmountMap["EasyHome"] = 40
        typeAmountMap["BikeIjarah"] = 40

        //initializing colors for the entries
        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#32764B"))
        colors.add(Color.parseColor("#CAA62B"))
        colors.add(Color.parseColor("#6C52AA"))
        colors.add(Color.parseColor("#2477AB"))
        colors.add(Color.parseColor("#BB00DA"))


        //input data and fit data into pie chart entry
        for (type in typeAmountMap.keys) {
            pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat(), type))
        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)

        binding.pieChart.apply {
            isRotationEnabled = false
            description.isEnabled = false
            setDrawSliceText(false) //text inside pie charts
            rotationAngle = 180f
            maxAngle = 180f
            legend.isEnabled = false
            data = pieData
            invalidate()
        }
    }

}