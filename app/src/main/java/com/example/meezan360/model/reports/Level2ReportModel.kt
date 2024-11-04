package com.example.meezan360.model.reports

import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.reports.RatingModels.Rating
import com.example.meezan360.model.reports.RatingModels.RatingDetail
import com.example.meezan360.model.reports.cards.Table
import com.google.gson.annotations.SerializedName

data class Level2ReportModel(
    @SerializedName("top_menu") var topMenu: String? = null,
    @SerializedName("boxes") var boxes: ArrayList<TopBoxesModel> = arrayListOf(),
    @SerializedName("rating") var rating: Rating? = null,
    @SerializedName("rating_detail") var rating_detail: List<RatingDetail>? = null,
    @SerializedName("report") var report: ArrayList<Report> = arrayListOf(),
    @SerializedName("table") var table:  ArrayList<Table> = arrayListOf(),
    @SerializedName("footer_boxes") var footerBoxes: ArrayList<FooterBoxes> = arrayListOf()
    ){

    fun toReport(): ArrayList<Report> {
        val convertedReports = ArrayList<Report>()
        table.forEach {
            val cardList = it.card ?: ArrayList()
            convertedReports.add(
                Report(
                    it.table_id,
                    it.table_title,
                    it.is_clickable,
                    it.inner_report_type,
                    it.column,
                    cardList
                )
            )
        }
        return convertedReports
    }


}
