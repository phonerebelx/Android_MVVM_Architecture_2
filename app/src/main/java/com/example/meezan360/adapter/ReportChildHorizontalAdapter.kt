package com.example.meezan360.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.LineChartItemBinding
import com.example.meezan360.databinding.ReportItemChildHorizontalBinding
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.reports.ReportsColumnData
import com.example.meezan360.utils.Utils


class ReportChildHorizontalAdapter(
    val myContext: Context,
    private val itemList: ArrayList<ReportsColumnData>,
    val onTtypeItemClickListener: OnTypeItemClickListener
) :
    RecyclerView.Adapter<ReportChildHorizontalAdapter.ViewHolder>() {
    private var selectedPosition = 0
    private lateinit var binding: ReportItemChildHorizontalBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ReportItemChildHorizontalBinding.inflate(LayoutInflater.from(myContext), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList[position]

        binding.tvChild.text = item.value
        binding.tvChild.setTextColor(Utils.parseColorSafely(item.valueColor))

        binding.llHorizontal.setOnClickListener {
            onTtypeItemClickListener.onClick("",itemList,position)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            this.setIsRecyclable(false)
        }
        var tvChild: TextView? = itemView.findViewById(R.id.tvChild)
        var llHorizontal: LinearLayout? = itemView.findViewById(R.id.llHorizontal)
    }
}