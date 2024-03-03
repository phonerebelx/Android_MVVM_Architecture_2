package com.example.meezan360.model.reports

import com.google.gson.annotations.SerializedName

data class Column(
    @SerializedName("header") var header: String,
    @SerializedName("identifier_type") var identifier_type: String,
    @SerializedName("data") var data: ArrayList<ReportsColumnData>? = arrayListOf()
)
