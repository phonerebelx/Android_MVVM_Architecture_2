package com.example.meezan360.model.footerGraph

import com.example.meezan360.model.footerGraph.data.MyLegend
import com.example.meezan360.model.footerGraph.data.StackChartDataModel
import com.google.gson.annotations.SerializedName

data class StackGraphModel(
    @SerializedName("type") var type: String,
    @SerializedName("label") var label: String,
    @SerializedName("data") var stackChartData: ArrayList<StackChartDataModel> = arrayListOf(),
    @SerializedName("legend") var legend: ArrayList<MyLegend> = arrayListOf()
)
