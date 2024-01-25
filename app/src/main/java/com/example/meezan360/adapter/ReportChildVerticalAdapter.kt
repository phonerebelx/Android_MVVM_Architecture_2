package com.example.meezan360.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.reports.ReportDataArrayModel
import com.example.meezan360.model.reports.ReportsColumnData
import com.example.meezan360.ui.activities.ReportLevel2Activity
import com.google.android.material.card.MaterialCardView

class ReportChildVerticalAdapter(
    private val myContext: Context,
    private val dataArrayList: ArrayList<ReportsColumnData>,
    private val columnList: ArrayList<ReportDataArrayModel>,
    private val tableId: Int,
) : RecyclerView.Adapter<ReportChildVerticalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_item_child_vertical, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataArrayList[position]

        val horizontalAdapter = ReportChildHorizontalAdapter(myContext, columnList[position].data)
        holder.rvHorizontalChild.adapter = horizontalAdapter
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        var rvHorizontalChild: RecyclerView = itemView.findViewById(R.id.rvHorizontalChild)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {

            val intent = Intent(view?.context, ReportLevel2Activity::class.java)
            intent.putExtra("kpiId", "1") //because this case is only for deposit
            intent.putExtra("tableId", tableId)
            view?.context?.startActivity(intent)
        }
    }
}
