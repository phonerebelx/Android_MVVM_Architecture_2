package com.example.meezan360.model.footerGraph

import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.google.gson.annotations.SerializedName

data class InvertedHorizontalGraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("label") var label: String,
    @SerializedName("top") var top: ArrayList<HorizontalBarChartDataModel> = arrayListOf(),
    @SerializedName("bottom") var bottom: ArrayList<HorizontalBarChartDataModel> = arrayListOf()
)
