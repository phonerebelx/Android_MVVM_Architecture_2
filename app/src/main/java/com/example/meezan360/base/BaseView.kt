package com.uhfsolutions.carlutions.base

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.IdRes

interface BaseView {
    fun closeDrawer()
    fun showBanner(text: String, type: String)
    fun navigateToFragment(@IdRes id: Int, args: Bundle? = null)
    fun setTitle(text: String)
    fun <T> initiateListArrayAdapter(list: List<T>): ArrayAdapter<T>
}