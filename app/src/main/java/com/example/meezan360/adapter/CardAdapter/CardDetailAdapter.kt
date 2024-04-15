package com.example.meezan360.adapter.CardAdapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentCardDetailAdapterBinding
import com.example.meezan360.databinding.ItemAccountDetailAdapterBinding
import com.example.meezan360.model.CardLevelModel.Data

class CardDetailAdapter(val context: Context) : RecyclerView.Adapter<CardDetailAdapter.ViewHolder>() {
    lateinit var binding: FragmentCardDetailAdapterBinding
    lateinit var adapterList: List<Data>
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Data?) {
            this.setIsRecyclable(false)
            binding.tvKey.text = item?.key
            binding.tvValue.text = item?.value.toString()
        }
    }
    fun setList(list: List<Data>) {
        adapterList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = FragmentCardDetailAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = adapterList[position]

        holder.bindItems(item)
    }

    override fun getItemCount(): Int {
        return when {
            ::adapterList.isInitialized -> adapterList.size
            else -> 0
        }
    }




}