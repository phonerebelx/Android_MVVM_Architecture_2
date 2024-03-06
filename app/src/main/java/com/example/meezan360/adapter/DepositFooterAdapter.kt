package com.example.meezan360.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.BarChartItemBinding
import com.example.meezan360.databinding.DepositReportFooterItemBinding
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.reports.FooterBoxes


class DepositFooterAdapter(
    private var myContext: Context,
    private val footerList: ArrayList<FooterBoxes>?,
    private val onTypeItemClickListener: OnTypeItemClickListener
) :
    RecyclerView.Adapter<DepositFooterAdapter.ViewHolder>() {
    private lateinit var binding: DepositReportFooterItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DepositReportFooterItemBinding.inflate(LayoutInflater.from(myContext), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = footerList?.get(position)
        holder.tvTitle?.text = item?.title
        holder.cvFooterItem?.setOnClickListener {
            onTypeItemClickListener.onClick("On_Deposit_Footer",item)
        }
    }

    override fun getItemCount(): Int {
        return footerList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView? = itemView.findViewById(R.id.tvTitle)
        var cvFooterItem: CardView? = itemView.findViewById(R.id.cvFooterItem)
    }
}