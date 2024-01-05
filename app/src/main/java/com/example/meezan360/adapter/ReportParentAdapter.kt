package com.example.meezan360.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.reports.Report


class ReportParentAdapter(
    private var myContext: Context,
    private val reportList: ArrayList<Report>?,
) :
    RecyclerView.Adapter<ReportParentAdapter.ViewHolder>() {
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.report_item_parent, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reportList?.get(position)
        holder.tvTitle.text = item?.tableTitle

        if (item == null) {
            return
        }

        val verticalAdapter =
            ReportChildVerticalAdapter(myContext, item.column[0].data, item.column)
        holder.rvVerticalChild.adapter = verticalAdapter

        val verticalAdapterHeader =
            ReportChildVerticalAdapter(myContext, item.column[0].data, item.column)
        holder.rvVerticalChildHeaders.adapter = verticalAdapterHeader
    }

    override fun getItemCount(): Int {
        return reportList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val rvVerticalChild: RecyclerView = itemView.findViewById(R.id.rvVerticalChild)
        val rvVerticalChildHeaders: RecyclerView =
            itemView.findViewById(R.id.rvVerticalChildHeaders)
    }
}