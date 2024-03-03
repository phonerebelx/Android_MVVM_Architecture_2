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
import com.example.meezan360.databinding.BarChartItemBinding
import com.example.meezan360.databinding.TopBoxItemBinding
import com.example.meezan360.interfaces.OnItemClickListener

class BarChartAdapter(
    val context: Context,
    private val itemList: List<String>?,
    private var onItemClick: OnItemClickListener
) :
    RecyclerView.Adapter<BarChartAdapter.ViewHolder>() {
    private lateinit var binding: BarChartItemBinding
    private var selectedPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = BarChartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList?.get(position)
        holder.text?.text = item

        if (position == selectedPosition) {
            holder.cardView?.setCardBackgroundColor(Color.parseColor("#856BC1"))
            holder.text?.setTextColor(Color.WHITE)
        } else {
            holder.cardView?.setCardBackgroundColor(Color.WHITE)
            holder.text?.setTextColor(ContextCompat.getColor(context, R.color.grey2))
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
        var text: TextView? = itemView.findViewById(R.id.text)
        var cardView: CardView? = itemView.findViewById(R.id.cardView)



    }

}