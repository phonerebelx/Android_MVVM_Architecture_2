package com.example.meezan360.model.footerGraph.data

import com.google.gson.annotations.SerializedName

data class MyLegend(
    @SerializedName("legend_value") var legendValue: String? = null,
    @SerializedName("legend_color") var legendColor: String? = null
)
