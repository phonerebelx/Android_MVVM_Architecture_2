package com.example.meezan360.model.dashboardByKpi

import com.google.gson.annotations.SerializedName

data class DataModel(
    @SerializedName("card_id")
    val cardId: String,
    @SerializedName("card_title")
    val cardTitle: String,
    @SerializedName("card_type")
    val cardType: String,
    @SerializedName("card_type_id")
    val cardTypeId: String,
    @SerializedName("is_percent")
    val isPercent: String,
    @SerializedName("is_vertical_legend")
    val isVerticalLegend: String
)