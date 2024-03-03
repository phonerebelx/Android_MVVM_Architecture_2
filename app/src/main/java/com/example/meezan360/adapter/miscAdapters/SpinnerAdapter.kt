package com.example.meezan360.adapter.miscAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.meezan360.R


class SpinnerAdapter(val context: Context, var listItemsTxt: List<String>) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.custom_spinner_item, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }


        // setting adapter item height programatically.

//        val params = view.layoutParams
//        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        view.layoutParams = params

        vh.label.text = listItemsTxt[position]
        vh.productName = listItemsTxt[position]
        return view
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {
        val label: TextView
        lateinit var productName: String
        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as TextView
        }
    }
}