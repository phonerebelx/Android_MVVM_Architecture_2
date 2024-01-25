package com.example.meezan360.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.reports.ReportsColumnData
import com.example.meezan360.utils.Utils


class ReportChildHorizontalAdapter(
    val myContext: Context,
    private val itemList: ArrayList<ReportsColumnData>,
) :
    RecyclerView.Adapter<ReportChildHorizontalAdapter.ViewHolder>() {
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.report_item_child_horizontal, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList[position]
        holder.tvChild.text = item.value
        holder.tvChild.setTextColor(Utils.parseColorSafely(item.valueColor))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvChild: TextView = itemView.findViewById(R.id.tvChild)
    }
}