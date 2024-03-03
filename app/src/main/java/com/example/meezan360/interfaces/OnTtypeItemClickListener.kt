package com.example.meezan360.interfaces

interface OnTypeItemClickListener {

    fun <T> onClick(type: String, item: T, position: Int = 0, checked: Boolean? = null)
}