package com.example.meezan360.model.footerGraph

import com.example.meezan360.model.footerGraph.data.BarChartDataModel
import com.google.gson.annotations.SerializedName

data class BarGraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("label") var label: String,
    @SerializedName("data") var barChartModel: ArrayList<BarChartDataModel>
)