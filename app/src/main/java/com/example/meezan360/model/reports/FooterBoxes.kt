package com.example.meezan360.model.reports

import com.google.gson.annotations.SerializedName

data class FooterBoxes(
    @SerializedName("table_id") var tableId: Int,
    @SerializedName("is_clickable") var isClickable: String,
    @SerializedName("inner_report_type") var innerReportType: String,
    @SerializedName("is_sub_value") var isSubValue: String,
    @SerializedName("title") var title: String
)
