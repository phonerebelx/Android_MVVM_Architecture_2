package com.example.meezan360.model.graphs


import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.google.gson.annotations.SerializedName

data class Pie1HorizontalBar1Model(
    @SerializedName("graph1") var graph1: PieGraphModel,
    @SerializedName("graph2") var graph2: HorizontalGraphModel,
)