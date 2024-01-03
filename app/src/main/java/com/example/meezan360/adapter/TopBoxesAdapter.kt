package com.example.meezan360.adapter

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel

class TopBoxesAdapter(val context: Context,private val itemList: List<TopBoxesModel>?) :
    RecyclerView.Adapter<TopBoxesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.top_box_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPos = itemList?.get(position)

        var valueColor = ContextCompat.getColor(context, R.color.grey2)
        if (!TextUtils.isEmpty(myPos?.valueColor)) {
            valueColor = Color.parseColor(myPos?.valueColor)
        }

        var percentageColor = Color.parseColor("#765CB4")
        if (!TextUtils.isEmpty(myPos?.percentageColor)) {
            percentageColor = Color.parseColor(myPos?.percentageColor)
        }

        var uomColor = Color.parseColor("#1F753E")
        if (!TextUtils.isEmpty(myPos?.uomColor)) {
            uomColor = Color.parseColor(myPos?.uomColor)
        }

        holder.tvTitle.text = myPos?.title
        holder.tvTitle.setTextColor(Color.parseColor(myPos?.titleColor))
        holder.tvAmount.text = myPos?.value
        holder.tvAmount.setTextColor(valueColor)
        holder.tvPercentage.text = myPos?.percentage
        holder.tvPercentage.setTextColor(percentageColor)
        holder.tvMin.text = myPos?.uom
        holder.tvMin.setTextColor(uomColor)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvAmount: TextView
        var tvPercentage: TextView
        var tvMin: TextView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvAmount = itemView.findViewById(R.id.tvAmount)
            tvPercentage = itemView.findViewById(R.id.tvPercentage)
            tvMin = itemView.findViewById(R.id.tvMin)
        }
    }

}