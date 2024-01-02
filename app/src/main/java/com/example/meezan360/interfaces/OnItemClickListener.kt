package com.example.meezan360.interfaces

interface OnItemClickListener {
    fun onClick(item: String?, position: Int, checked: Boolean? = null)
}