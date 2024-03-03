package com.example.meezan360.model.reports.RatingModels

data class Rating(
    val rating_data: ArrayList<RatingData> = arrayListOf(),
    val rating_footer: String = "",
    val rating_title: String = ""
)