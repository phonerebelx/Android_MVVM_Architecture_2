package com.example.architecture.micsAdapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.architecture.R


class SearchSpinnerAdapter(val context: Context, var listItemsTxt: List<String>) : BaseAdapter(), Filterable {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var filteredList: List<String> = listItemsTxt

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.custom_spinner_item, parent, false)
            vh = ItemRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        vh.label.text = filteredList[position] // Use filteredList instead of listItemsTxt
        return view
    }

    override fun getItem(position: Int): Any? {
        return filteredList[position] // Use filteredList instead of listItemsTxt
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return filteredList.size // Use filteredList instead of listItemsTxt
    }

    private class ItemRowHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.txtDropDownLabel) as TextView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    results.values = listItemsTxt // Reset to original list if no filter constraint
                } else {
                    val filtered = listItemsTxt.filter { it.lowercase().startsWith(constraint.toString().lowercase()) }
                    results.values = filtered
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    filteredList = results.values as? List<String> ?: listOf()
                    Log.d("publishResults: ", filteredList.toString())
                    notifyDataSetChanged()
                }
            }
        }
    }
}
