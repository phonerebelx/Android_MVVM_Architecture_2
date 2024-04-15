package com.example.meezan360.adapter.levelTwoAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.adapter.BarChartAdapter
import com.example.meezan360.databinding.BarChartItemBinding
import com.example.meezan360.databinding.StepProgressBarItemBinding
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.loukwn.stagestepbar.StageStepBar

class StepProgressBarAdapter(
    val context: Context,
    private val itemList: ArrayList<HorizontalBarChartDataModel>,
) :
    RecyclerView.Adapter<StepProgressBarAdapter.ViewHolder>() {
    private lateinit var binding: StepProgressBarItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = StepProgressBarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemList[position]
        holder.tvRemarks?.text = item.key
        holder.tvBranches?.text = " (No of Branches ${item?.value?.toInt().toString()} | ${item.percentage}) "

        val reminder = item.percentage?.rem(20)
        val division = item.percentage?.div(20)

        if (division != null && reminder != null) {

            if (item.percentage!! < 20f) {
                holder.stageStepBar?.setCurrentState(null)
            } else {
                holder.stageStepBar?.setCurrentState(
                    StageStepBar.State(
                        division.toInt()-1,
                        reminder.toInt()
                    )
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(private var itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRemarks: TextView? = itemView.findViewById(R.id.tvRemarks)
        var tvBranches: TextView? = itemView.findViewById(R.id.tvBranches)
//        var btnNoOfDays: Button? = itemView.findViewById(R.id.btnNoOfDays)
        var stageStepBar: StageStepBar? = itemView.findViewById(R.id.stageStepBar)


    }

}