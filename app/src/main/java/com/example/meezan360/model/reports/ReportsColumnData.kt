package com.example.meezan360.model.reports

import com.google.gson.annotations.SerializedName

data class ReportsColumnData(
    @SerializedName("value") var value: String,
    @SerializedName("value_color") var valueColor: String
)
