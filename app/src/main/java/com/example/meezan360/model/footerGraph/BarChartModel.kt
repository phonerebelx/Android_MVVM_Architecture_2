package com.example.meezan360.model.footerGraph

import com.google.gson.annotations.SerializedName

data class BarChartModel(
    @SerializedName("key") var key: Int,
    @SerializedName("value") var value: Double,
    @SerializedName("value_color") var valueColor: String
)