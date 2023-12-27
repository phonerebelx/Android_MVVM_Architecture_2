package com.example.meezan360.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel

class BarChartAdapter(
    private val itemList: List<TopBoxesModel>?,
    private var onItemClick: OnItemClickListener
) :
    RecyclerView.Adapter<BarChartAdapter.ViewHolder>() {

    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bar_chart_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList?.get(position)
        holder.text.text = item?.title

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#856BC1"))
            holder.text.setTextColor(Color.WHITE)
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
            holder.text.setTextColor(Color.BLACK)
        }

        holder.text.setOnClickListener {
            selectedPosition = holder.adapterPosition
            onItemClick.onClick(item, position)
        }


    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ViewHolder(private var itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.text)
        var cardView: CardView = itemView.findViewById(R.id.cardView)

    }

}