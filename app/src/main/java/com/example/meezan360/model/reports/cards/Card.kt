package com.example.meezan360.model.reports.cards

data class Card(
    val cif_id: String,
    val customer_name: String,
    val last_day: LastDay,
    val last_month: LastMonth,
    val last_year: LastYear,
    val level_name: String,
    val level_value: String
)