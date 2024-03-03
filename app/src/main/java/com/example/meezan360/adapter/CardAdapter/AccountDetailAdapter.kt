package com.example.meezan360.adapter.CardAdapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.FragmentCardLevelAdapterBinding
import com.example.meezan360.databinding.ItemAccountDetailAdapterBinding
import com.example.meezan360.model.reports.cards.Card

class AccountDetailAdapter(val context: Context) : RecyclerView.Adapter<AccountDetailAdapter.ViewHolder>() {

    lateinit var binding: ItemAccountDetailAdapterBinding
    lateinit var adapterList: List<String>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: String?) {
            this.setIsRecyclable(false)

            binding.tvCifId.text = item.toString()
        }
    }
    fun setList(list: List<String>) {

        adapterList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAccountDetailAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
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
    }


}