package com.example.meezan360.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.LineChartItemBinding
import com.example.meezan360.databinding.ProductWiseItemBinding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.model.footerGraph.HorizontalGraphModel
import com.example.meezan360.utils.Utils
import com.google.android.material.checkbox.MaterialCheckBox


class LineChartAdapter(
    val context: Context,
    private val itemList: List<HorizontalGraphModel>?,
    private var onItemClick: OnItemClickListener
) :
    RecyclerView.Adapter<LineChartAdapter.ViewHolder>() {
    private lateinit var binding: LineChartItemBinding
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LineChartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList?.get(position)
        holder.tvTitle?.text = item?.label
        holder.checkbox?.buttonTintList = ColorStateList.valueOf(Utils.parseColorSafely(item?.color))
        //for first checkbox to be selected
            holder.checkbox?.isChecked = true


        holder.checkbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            onItemClick.onClick(item?.label, position, isChecked)
        }

    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView? = itemView.findViewById(R.id.tvTitle)
        var checkbox: MaterialCheckBox? = itemView.findViewById(R.id.checkbox)

    }
}