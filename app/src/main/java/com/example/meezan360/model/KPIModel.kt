package com.example.meezan360.model

import com.google.gson.annotations.SerializedName

data class KPIModel(
    val kpis: List<Kpi>,
    @SerializedName("latest_version")
    val latestVersion: String
)