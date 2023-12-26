package com.example.meezan360.model.footerGraph.data

import com.google.gson.annotations.SerializedName

data class PieChartDataModel(
    @SerializedName("value") var value: Float,
    @SerializedName("color") var color: String
)