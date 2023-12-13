package com.example.meezan360.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pyramid
import com.anychart.core.ui.Legend
import com.anychart.enums.LegendLayout
import com.example.meezan360.databinding.FragmentTierWiseDepositBinding


class TierWiseDepositFragment : Fragment() {

    private lateinit var binding: FragmentTierWiseDepositBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTierWiseDepositBinding.inflate(layoutInflater)

//        setupPyramidChart()

        return binding.root
    }

//    private fun setupPyramidChart() {
//        val pyramid: Pyramid = AnyChart.pyramid()
//
//        val data: MutableList<DataEntry> = ArrayList()
//        data.add(ValueDataEntry("TV promotion", 6371664))
//        data.add(ValueDataEntry("Radio promotion", 7216301))
//        data.add(ValueDataEntry("Advertising leaflets", 1486621))
//        data.add(ValueDataEntry("Before advertising started", 1386622))
//
//        pyramid.data(data)
//
//        val legend: Legend = pyramid.legend()
//        legend.enabled(true)
//        legend.position("outside-right")
//        legend.itemsLayout(LegendLayout.VERTICAL)
//        legend.align(Align.TOP)
//
//        pyramid.labels(false)
//
//        binding.anyChart.setChart(pyramid)
//    }

}