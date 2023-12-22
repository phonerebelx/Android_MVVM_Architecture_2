package com.example.meezan360.model.dashboardByKpi

import com.google.gson.annotations.SerializedName

data class FooterModel(
    @SerializedName("data")
    val dataModel: List<DataModel>,
    @SerializedName("tab_name")
    val tabName: String
)