package com.example.meezan360.model.footerGraph.data

import com.google.gson.annotations.SerializedName

data class TierChartDataModel(
    @SerializedName("key") var key: String,
    @SerializedName("value") var value: Double,
    @SerializedName("volumn") var volumn: Double? = null,
    @SerializedName("color") var color: String
)
