package com.example.meezan360.model.footerGraph

import com.google.gson.annotations.SerializedName

data class GraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("label") var label: String,
    @SerializedName("data") var pieChartModel: PieChartModel
)