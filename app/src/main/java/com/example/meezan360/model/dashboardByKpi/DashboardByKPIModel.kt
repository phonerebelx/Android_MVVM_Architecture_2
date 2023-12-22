package com.example.meezan360.model.dashboardByKpi

import com.google.gson.annotations.SerializedName

data class DashboardByKPIModel(
    @SerializedName("footer")
    val footer: List<FooterModel>,
    @SerializedName("top_boxes")
    val topBoxes: List<TopBoxesModel>
)