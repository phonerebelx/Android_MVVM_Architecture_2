package com.example.meezan360.model.footerGraph

import com.example.meezan360.model.footerGraph.data.BarChartDataModel
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.google.gson.annotations.SerializedName

data class HorizontalGraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("data") var barChartModel: ArrayList<HorizontalBarChartDataModel>
)
