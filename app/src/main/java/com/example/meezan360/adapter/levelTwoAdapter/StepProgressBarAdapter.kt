package com.example.meezan360.adapter.levelTwoAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.loukwn.stagestepbar.StageStepBar

class StepProgressBarAdapter(
    val context: Context,
    private val itemList: ArrayList<HorizontalBarChartDataModel>,
) :
    RecyclerView.Adapter<StepProgressBarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.step_progress_bar_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList[position]
        holder.tvRemarks.text = item.key
        holder.tvBranches.text = " (No of Branches ${item?.value?.toInt().toString()})"
        val stage = when {
            item.percentage!! in 0.0f..33.33f -> 0
            item.percentage!! in 33.33f..66.66f -> 1
            item.percentage!! in 66.66f..100.0f -> 2
            else -> 0
        }
        holder.stageStepBar.setCurrentState(StageStepBar.State(stage, item.percentage!!.toInt()))

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(private var itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRemarks: TextView = itemView.findViewById(R.id.tvRemarks)
        var tvBranches: TextView = itemView.findViewById(R.id.tvBranches)
        var btnNoOfDays: Button = itemView.findViewById(R.id.btnNoOfDays)
        var stageStepBar: StageStepBar = itemView.findViewById(R.id.stageStepBar)


    }

}