package com.example.meezan360.adapter.RatingAdapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.adapter.CardAdapter.CardLevelAdapter
import com.example.meezan360.databinding.FragmentCardLevelAdapterBinding
import com.example.meezan360.databinding.FragmentRatingLevelAdapterBinding
import com.example.meezan360.model.reports.RatingModels.RatingData
import com.example.meezan360.model.reports.cards.Card


class RatingLevelAdapter(val context: Context) :  RecyclerView.Adapter<RatingLevelAdapter.ViewHolder>(){

    private lateinit var binding: FragmentRatingLevelAdapterBinding
    lateinit var adapterList: ArrayList<RatingData>
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: RatingData?) {
            binding.tvRatingTitle.text = item?.title
            binding.tvRatingValue.text = item?.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = FragmentRatingLevelAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item  = adapterList[position]
        holder.bindItems(item)
    }
    override fun getItemCount(): Int {
        return when {
            ::adapterList.isInitialized -> adapterList.size
            else -> 0
        }
    }
}