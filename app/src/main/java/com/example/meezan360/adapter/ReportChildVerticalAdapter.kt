package com.example.meezan360.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.reports.Column
import com.example.meezan360.model.reports.Data


class ReportChildVerticalAdapter(
    private val myContext: Context,
    private val dataArrayList: ArrayList<Data>,
    private val columnList: ArrayList<Column>,
) :
    RecyclerView.Adapter<ReportChildVerticalAdapter.ViewHolder>() {
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.report_item_child_vertical, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataArrayList[position]

        val horizontalAdapter = ReportChildHorizontalAdapter(myContext, columnList)
        holder.rvHorizontalChild.adapter = horizontalAdapter
    }

    override fun getItemCount(): Int {
        return dataArrayList.size //for headers
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rvHorizontalChild: RecyclerView = itemView.findViewById(R.id.rvHorizontalChild)
    }
}