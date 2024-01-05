package com.example.meezan360.model.reports

import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.google.gson.annotations.SerializedName

data class DepositObject(
    @SerializedName("top_boxes") var topBoxes: ArrayList<TopBoxesModel> = arrayListOf(),
    @SerializedName("report") var report: ArrayList<Report> = arrayListOf(),
    @SerializedName("footer_boxes") var footerBoxes: ArrayList<FooterBoxes> = arrayListOf()
)
