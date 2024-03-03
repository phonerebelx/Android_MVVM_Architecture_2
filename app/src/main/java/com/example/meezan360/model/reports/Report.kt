package com.example.meezan360.model.reports

import android.annotation.SuppressLint
import com.example.meezan360.model.reports.cards.Card
import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("table_id") var tableId: String,
    @SerializedName("table_title") var tableTitle: String,
    @SerializedName("is_clickable") var isClickable: String,
    @SerializedName("inner_report_type") var innerReportType: String,
    @SerializedName("column") var column : ArrayList<Column>? = arrayListOf(),
    @SerializedName("card") var card:  ArrayList<Card> = arrayListOf(),
) {
    companion object {
        @SuppressLint("SuspiciousIndentation")
        fun getDataArray(columns: ArrayList<Column>): ArrayList<ReportDataArrayModel> {
            val result: ArrayList<ReportDataArrayModel> = arrayListOf()

                columns.first().data?.forEachIndexed { indexRow, data ->
                    val columData = ArrayList<ReportsColumnData>()
                    columns.forEachIndexed { index, column ->
                        if ((column.data?.count() ?: 0) > indexRow) {
                            column.data?.get(indexRow)?.let {
                                columData.add(it)
                            }
                        }
                    }
                    result.add(ReportDataArrayModel(columData))

                }

            return result
        }
    }
}

data class ReportDataArrayModel(
    var data: ArrayList<ReportsColumnData>
)

