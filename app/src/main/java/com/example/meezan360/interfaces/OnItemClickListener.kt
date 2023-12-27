package com.example.meezan360.interfaces

import com.example.meezan360.model.dashboardByKpi.TopBoxesModel

interface OnItemClickListener {
    fun onClick(item: TopBoxesModel?, position: Int)
}