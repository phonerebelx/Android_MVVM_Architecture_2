package com.example.meezan360.model.footerGraph

import com.google.gson.annotations.SerializedName

data class Pie2Bar2Model(
    @SerializedName("graph1") var graph1: GraphModel,
    @SerializedName("graph2") var graph2: GraphModel,
    @SerializedName("graph3") var graph3: GraphModel,
    @SerializedName("graph4") var graph4: GraphModel
)