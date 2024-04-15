package com.example.meezan360.model.graphs

import com.example.meezan360.model.footerGraph.PieGraphModel
import com.google.gson.annotations.SerializedName

data class PieChartModel(
    @SerializedName("graph1") var graph1: PieGraphModel? = null,
)
