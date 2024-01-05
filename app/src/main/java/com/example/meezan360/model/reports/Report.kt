package com.example.meezan360.model.reports

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("table_id") var tableId: Int,
    @SerializedName("table_title") var tableTitle: String,
    @SerializedName("is_clickable") var isClickable: String,
    @SerializedName("inner_report_type") var innerReportType: String,
    @SerializedName("column") var column: ArrayList<Column> = arrayListOf()
)
