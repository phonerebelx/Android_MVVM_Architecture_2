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
import com.example.meezan360.model.footerGraph.data.TierChartDataModel
import com.example.meezan360.utils.Utils

class HalfPieAdapter(
    val context: Context,
    private val itemList: List<TierChartDataModel>?,
) :
    RecyclerView.Adapter<HalfPieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.product_wise_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList?.get(position)
        holder.viewLine.setBackgroundColor(Utils.parseColorSafely(item?.color))
        holder.tvTitle.text = item?.key
        holder.tvPercentage.text = item?.value.toString()
        holder.tvPercentage.setTextColor(Utils.parseColorSafely(item?.color))
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ViewHolder(private var itemView: View) : RecyclerView.ViewHolder(itemView) {
        var viewLine: View = itemView.findViewById(R.id.view_line)
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)


    }

}