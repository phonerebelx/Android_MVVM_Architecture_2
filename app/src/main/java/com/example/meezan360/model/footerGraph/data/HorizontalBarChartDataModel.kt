package com.example.meezan360.model.footerGraph.data

import com.google.gson.annotations.SerializedName

data class HorizontalBarChartDataModel(
    @SerializedName("key") var key: String,
    @SerializedName("value") var value: Int,
    @SerializedName("percentage") var percentage: Int,
    @SerializedName("target") var target: Int,
    @SerializedName("value_color") var valueColor: String,
    @SerializedName("target_color") var targetColor: String
)
