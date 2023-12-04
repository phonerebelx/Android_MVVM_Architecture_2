package com.example.circlemenu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.circlemenu.model.DetailModel
import com.example.meezan360.R

class MyAdapter(private val itemList: ArrayList<DetailModel>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvTitle.text = itemList[position].title
      holder.tvAmount.text = itemList[position].amount
      holder.tvPercentage.text = itemList[position].percentage
      holder.tvMin.text = itemList[position].min
    }

    override fun getItemCount(): Int {
        return itemList.size
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