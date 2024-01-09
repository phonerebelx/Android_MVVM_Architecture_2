package com.example.meezan360.model.reports

import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.google.gson.annotations.SerializedName

data class Level2ReportModel(
    @SerializedName("top_menu") var topMenu: String? = null,
    @SerializedName("boxes") var boxes: ArrayList<TopBoxesModel> = arrayListOf(),
    @SerializedName("table") var table: ArrayList<Report> = arrayListOf()
)
