package com.example.meezan360.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentCardLevelAdapterBinding
import com.example.meezan360.databinding.TopBoxItemBinding
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.utils.Utils

class TopBoxesAdapter(val context: Context, private val itemList: List<TopBoxesModel>?) :
    RecyclerView.Adapter<TopBoxesAdapter.ViewHolder>() {

        private lateinit var binding: TopBoxItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = TopBoxItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPos = itemList?.get(position)

        val valueColor = Utils.parseColorSafely(myPos?.valueColor)

        val percentageColor = Utils.parseColorSafely(myPos?.percentageColor)

        val uomColor = Utils.parseColorSafely(myPos?.uomColor)

        binding.tvTitle.text = myPos?.title ?: ""
        binding.tvTitle.setTextColor(Utils.parseColorSafely(myPos?.titleColor) ?: Utils.parseColorSafely("") )
        binding.tvAmount.text = myPos?.value ?: ""
        binding.tvAmount.setTextColor(valueColor) ?: ""
        binding.tvPercentage.text = myPos?.percentage ?: ""
        binding.tvPercentage.setTextColor(percentageColor ?: Utils.parseColorSafely(""))
        binding.tvMin.text = myPos?.uom ?: ""
        binding.tvMin.setTextColor(uomColor ?:  Utils.parseColorSafely(""))
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