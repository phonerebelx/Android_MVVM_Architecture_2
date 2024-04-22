package com.example.meezan360.adapter.CardAdapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentCardLevelAdapterBinding
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.reports.cards.Card

class CardLevelAdapter(val context: Context,val onTypeItemClickListener: OnTypeItemClickListener) : RecyclerView.Adapter<CardLevelAdapter.ViewHolder>() {

    lateinit var binding: FragmentCardLevelAdapterBinding
    lateinit var adapterList: ArrayList<Card>
    private var getDataCount: Int = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Card?) {
            this.setIsRecyclable(false)

            //last day detail
            binding.tvRegionDetail.text = item?.level_value
            binding.tvCustomerDetail.text = item?.customer_name
            binding.tvAreaDetail.text = item?.area
            binding.tvBranchDetail.text = item?.branch
            when (item?.last_day) {
                null -> {
                    binding.llLastDay.visibility = View.GONE
                }

                else -> {
                    // Day Detail
                    getDataCount++
                    binding.tvLastDayDetail.text = item.last_day?.last
                    binding.tvTodayDetail.text = item.last_day?.today
                    binding.tvDayDeltaDetail.text = item?.last_day?.delta

                    val color = if (item.last_day?.delta_color != null) {
                        Color.parseColor(item.last_day.delta_color)
                    } else {
                        ContextCompat.getColor(context, R.color.dark_grey)
                    }
                    binding.tvDayDeltaDetail.setBackgroundColor(color)
                }
            }

            when (item?.last_month) {
                null -> {
                    binding.llLastMonth.visibility = View.GONE
                }

                else -> {
//                    last month detail
                    getDataCount++
                    binding.tvLastMonthDetail.text = item.last_month?.last
                    binding.tvMonthTodayDetail.text = item.last_month?.today
                    val color = if (item.last_month?.delta_color != null) {
                        Color.parseColor(item.last_month.delta_color)
                    } else {
                        ContextCompat.getColor(context, R.color.dark_grey)
                    }
                    binding.tvMonthDeltaDetail.setBackgroundColor(color)
                    binding.tvMonthDeltaDetail.text = item.last_month?.delta
                }
            }

            when (item?.last_year) {
                null -> {
                    binding.llLastYear.visibility = View.GONE
                }

                else -> {
                    //last year detail
                    getDataCount++
                    binding.tvLastYearDetail.text = item.last_year?.last
                    binding.tvYearTodayDetail.text = item.last_year?.today
                    val color = if (item.last_year?.delta_color != null) {
                        Color.parseColor(item.last_year.delta_color)
                    } else {
                        ContextCompat.getColor(context, R.color.dark_grey)
                    }
                    binding.tvYearDeltaDetail.setBackgroundColor(color)
                    binding.tvYearDeltaDetail.text = item.last_year?.delta
                }
            }
            if (getDataCount == 1) {
                getDataCount = 0
                val layoutHeight =
                    context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
                binding.cvCardBox.layoutParams.height = layoutHeight
                binding.cvCardBox.layoutParams = binding.cvCardBox.layoutParams
            } else if (getDataCount == 2) {
                getDataCount = 0
                val layoutHeight =
                    context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._190sdp)
                binding.cvCardBox.layoutParams.height = layoutHeight
                binding.cvCardBox.layoutParams = binding.cvCardBox.layoutParams
            } else if (getDataCount == 3) {
                getDataCount = 0
                val layoutHeight =
                    context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._200sdp)
                binding.cvCardBox.layoutParams.height = layoutHeight
                binding.cvCardBox.layoutParams = binding.cvCardBox.layoutParams
            }

        }
    }

    fun setList(list: ArrayList<Card>) {
        adapterList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = FragmentCardLevelAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return when {
            ::adapterList.isInitialized -> adapterList.size
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = adapterList[position]


        holder.bindItems(item)

        binding.cvCardBox.setOnClickListener {
            onTypeItemClickListener.onClick("On_Card_Item_Click",item)
        }
    }


}