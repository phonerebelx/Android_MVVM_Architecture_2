package com.example.meezan360.model.graphs


import com.example.meezan360.model.footerGraph.BarGraphModel
import com.example.meezan360.model.footerGraph.PieGraphModel
import com.google.gson.annotations.SerializedName

data class Pie2Bar2Model(
    @SerializedName("graph1") var graph1: PieGraphModel? = null,
    @SerializedName("graph2") var graph2: BarGraphModel? = null,
    @SerializedName("graph3") var graph3: PieGraphModel? = null,
    @SerializedName("graph4") var graph4: BarGraphModel? = null
)