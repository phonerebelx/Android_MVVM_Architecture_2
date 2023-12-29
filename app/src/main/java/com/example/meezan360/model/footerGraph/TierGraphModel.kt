package com.example.meezan360.model.footerGraph

import com.example.meezan360.model.footerGraph.data.TierChartDataModel
import com.google.gson.annotations.SerializedName

data class TierGraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("label") var label: String,
    @SerializedName("data") var data: ArrayList<TierChartDataModel> = arrayListOf()
)
