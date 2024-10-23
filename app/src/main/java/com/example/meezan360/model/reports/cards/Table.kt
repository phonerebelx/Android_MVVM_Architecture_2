package com.example.meezan360.model.reports.cards

import com.example.meezan360.model.reports.Column
import com.google.gson.annotations.SerializedName

data class Table(
    val card: ArrayList<Card>,
    val inner_report_type: String,
    val is_clickable: String,
    val table_id: String,
    val table_title: String,
    val column: ArrayList<Column>,

    )