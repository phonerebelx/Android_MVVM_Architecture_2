package com.example.meezan360.model.footerGraph.data

import com.google.gson.annotations.SerializedName

data class BarChartDataModel(
    @SerializedName("key") var key: Int,
    @SerializedName("value") var value: Float,
    @SerializedName("value_color") var valueColor: String
)