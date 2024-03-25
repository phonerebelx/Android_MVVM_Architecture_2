package com.example.meezan360.model.footerGraph.data

import com.google.gson.annotations.SerializedName

data class StackChartDataModel(
    @SerializedName("key") var key: String,
    @SerializedName("value1") var value1: Int,
    @SerializedName("value2") var value2: Int,
    @SerializedName("value3") var value3: Int,
    @SerializedName("value1_color") var value1Color: String,
    @SerializedName("value2_color") var value2Color: String,
    @SerializedName("value3_color") var value3Color: String
)
