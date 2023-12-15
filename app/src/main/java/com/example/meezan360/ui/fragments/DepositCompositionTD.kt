package com.example.meezan360.ui.fragments

import PieChartBuilder
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentDepositCompositionTdBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieEntry


class DepositCompositionTD : Fragment() {

    private lateinit var binding: FragmentDepositCompositionTdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositCompositionTdBinding.inflate(layoutInflater)

        showPieChart()

        return binding.root
    }

    private fun showPieChart() {
        val pieEntriesSA = mutableListOf(PieEntry(75f), PieEntry(25f))
        val colorsSA = arrayListOf(Color.parseColor("#856BC1"), Color.parseColor("#FAFAFA"))

        val legendEntriesSA = arrayOf(
            LegendEntry(
                "75%",
                Legend.LegendForm.LINE,
                75f,
                15f,
                null,
                ContextCompat.getColor(requireContext(), R.color.purple_light)
            ),
            LegendEntry(
                "25%",
                Legend.LegendForm.LINE,
                25f,
                15f,
                null,
                ContextCompat.getColor(requireContext(), R.color.purple_light)
            )
        )

        PieChartBuilder(requireContext(), binding.pieChartSA)
            .description("SA")
            .xOffset(-70f)
            .yOffset(40f)
            .centerText("75%")
            .extraRightOffset(70f)
            .holeColor(Color.parseColor("#E0E0E0"))
            .centerTextSize(20f)
            .centerTextColor(Color.parseColor("#7B7878"))
            .pieEntries(pieEntriesSA)
            .colors(colorsSA)
            .legendEnabled(true)
            .legendAlignment(Legend.LegendHorizontalAlignment.RIGHT)
            .legendEntries(legendEntriesSA)
            .build()

    }

}