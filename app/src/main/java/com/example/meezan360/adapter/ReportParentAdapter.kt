package com.example.meezan360.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.reports.Report


class ReportParentAdapter(
    private var myContext: Context,
    private val reportList: ArrayList<Report>?,
) : RecyclerView.Adapter<ReportParentAdapter.ViewHolder>() {
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

        //it creates textview for header dynamically on the basis of column items
        item.column.forEachIndexed { index, column ->
            val valueTV = TextView(myContext)
            valueTV.text = column.header
            valueTV.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                myContext.resources.getDimension(R.dimen.text_size_small)
            )
            valueTV.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            valueTV.setTextColor(myContext.resources.getColor(R.color.white))
            valueTV.layoutParams = LinearLayout.LayoutParams(
                myContext.resources.getDimension(com.intuit.sdp.R.dimen._65sdp).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
            holder.headerLayout.addView(valueTV)
        }

        val columnsData = Report.getDataArray(item.column)

        val verticalAdapter =
            item.column[0].data?.let {
                ReportChildVerticalAdapter(
                    myContext,
                    it,
                    columnsData,
                    item.tableId
                )
            }

        holder.rvVerticalChild.adapter = verticalAdapter

    }

    override fun getItemCount(): Int {
        return reportList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val rvVerticalChild: RecyclerView = itemView.findViewById(R.id.rvVerticalChild)
        val headerLayout: LinearLayout = itemView.findViewById(R.id.headerLayout)

    }
}