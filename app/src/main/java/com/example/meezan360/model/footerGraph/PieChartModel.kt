package com.example.meezan360.model.footerGraph

import com.google.gson.annotations.SerializedName

data class PieChartModel(
    @SerializedName("value") var value: Double,
    @SerializedName("color") var color: String
)