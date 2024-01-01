package com.example.meezan360.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.google.android.material.checkbox.MaterialCheckBox

class LineChartAdapter(
    val context: Context,
    private val itemList: List<String>?,
    private var onItemClick: OnItemClickListener
) :
    RecyclerView.Adapter<LineChartAdapter.ViewHolder>() {

    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.line_chart_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList?.get(position)
        holder.tvTitle.text = item

        if (position == selectedPosition) {
//            holder.cardView.setCardBackgroundColor(Color.parseColor("#856BC1"))
//            holder.text.setTextColor(Color.WHITE)
        } else {
//            holder.cardView.setCardBackgroundColor(Color.WHITE)
//            holder.text.setTextColor(ContextCompat.getColor(context, R.color.grey2))
        }

        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onItemClick.onClick(item, position)
        }


    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ViewHolder(private var itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var checkbox: MaterialCheckBox = itemView.findViewById(R.id.checkbox)

    }

}