package com.example.meezan360.adapter.levelTwoAdapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.adapter.ReportParentAdapter
import com.example.meezan360.databinding.ReportItemParentBinding
import com.example.meezan360.databinding.TopBoxCategoryItemBinding
import com.example.meezan360.interfaces.OnItemClickListener


class TopMenuAdapter(
    private var myContext: Context,
    private val reportList: ArrayList<String>,
    private var onItemClick: OnItemClickListener
) :
    RecyclerView.Adapter<TopMenuAdapter.ViewHolder>() {
    private var selectedPosition = 0
    lateinit var binding: TopBoxCategoryItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = TopBoxCategoryItemBinding.inflate(LayoutInflater.from(myContext), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reportList[position]

        holder.tvTitle?.text = item

        if (position == selectedPosition) {
            holder.tvTitle?.setBackgroundResource(R.drawable.purple_rounded_gradient)
            holder.tvTitle?.setTextColor(Color.WHITE)
        } else {
            holder.tvTitle?.setBackgroundResource(0)
            holder.tvTitle?.setTextColor(ContextCompat.getColor(myContext, R.color.grey2))
        }

        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onItemClick.onClick(item, position)
        }
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView? = itemView.findViewById(R.id.tvTopBoxTitle)
    }
}