package com.example.meezan360.model

import com.google.gson.annotations.SerializedName

data class Kpi(
    @SerializedName("clear_cacha")
    val clearCache: Boolean,
    @SerializedName("is_default")
    val isDefault: Boolean,
    @SerializedName("key_name")
    val keyName: String,
    @SerializedName("kpi_id")
    val kpiId: Int,
    @SerializedName("last_sync_date")
    val lastSyncDate: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("table_id")
    val tableId: Int
)