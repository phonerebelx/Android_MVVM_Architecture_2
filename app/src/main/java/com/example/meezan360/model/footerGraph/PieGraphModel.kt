package com.example.meezan360.model.footerGraph

import com.example.meezan360.model.footerGraph.data.PieChartDataModel
import com.google.gson.annotations.SerializedName

data class PieGraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("label") var label: String,
    @SerializedName("data") var pieChartModel: PieChartDataModel
)