package com.example.meezan360.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.ReportItemChildHorizontalBinding
import com.example.meezan360.databinding.ReportItemChildVerticalBinding

import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.reports.Report
import com.example.meezan360.model.reports.ReportDataArrayModel
import com.example.meezan360.model.reports.ReportsColumnData
import com.example.meezan360.model.reports.cards.Card
import com.example.meezan360.ui.activities.ReportLevel2Activity
import com.google.android.material.card.MaterialCardView

class ReportChildVerticalAdapter(
    private val myContext: Context,
    private val dataArrayList: ArrayList<ReportsColumnData>,
    private val columnList: ArrayList<ReportDataArrayModel>,
    private val tableId: String,
    val onTypeItemClickListener: OnTypeItemClickListener


) : RecyclerView.Adapter<ReportChildVerticalAdapter.ViewHolder>(),OnTypeItemClickListener {
    private lateinit var binding: ReportItemChildVerticalBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ReportItemChildVerticalBinding.inflate(LayoutInflater.from(myContext), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataArrayList[position]

        val horizontalAdapter = ReportChildHorizontalAdapter(myContext, columnList[position].data,this)
        binding.rvHorizontalChild?.adapter = horizontalAdapter

//        holder.cardView.setOnClickListener {
//            onTtypeItemClickListener.onClick("",item.value,position)
//        }


    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            this.setIsRecyclable(false)
        }
        var cardView: MaterialCardView? = itemView.findViewById(R.id.cardView)
        var rvHorizontalChild: RecyclerView? = itemView.findViewById(R.id.rvHorizontalChild)

    }

    override fun <T> onClick(type: String, item: T, position: Int, checked: Boolean?) {


        onTypeItemClickListener.onClick("Come From Vertical Child Adapter", item)

    }


}
