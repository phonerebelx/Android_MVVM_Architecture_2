package com.example.meezan360.model.dashboardByKpi

import com.google.gson.annotations.SerializedName

data class TopBoxesModel(
    @SerializedName("percentage")
    val percentage: String? = "",
    @SerializedName("percentage_color")
    val percentageColor: String? = "",
    @SerializedName("uom")
    val uom: String? = "",
    @SerializedName("uom_color")
    val uomColor: String? = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("title_color")
    val titleColor: String = "",
    @SerializedName("value")
    val value: String = "",
    @SerializedName("value_color")
    val valueColor: String = ""
)
