package com.example.meezan360.adapter.levelTwoAdapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.databinding.StepProgressBarItemBinding
import com.example.meezan360.model.footerGraph.data.HorizontalBarChartDataModel
import com.loukwn.stagestepbar.StageStepBar
import com.shuhart.stepview.StepView
import timber.log.Timber

//
//class StepProgressBarAdapter(
//    private val context: Context,
//    private val itemList: ArrayList<HorizontalBarChartDataModel>,
//) : RecyclerView.Adapter<StepProgressBarAdapter.ViewHolder>() {
//
//    private lateinit var binding: StepProgressBarItemBinding
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        binding = StepProgressBarItemBinding.inflate(LayoutInflater.from(context), parent, false)
//        return ViewHolder(binding.root)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = itemList[position]
//        holder.tvRemarks?.text = item.key
//        holder.tvBranches?.text = " (No of Branches ${item.value?.toInt().toString()} | ${item.percentage}) "
//
//        val percentage = item.percentage ?: 0f
//        val division = percentage.div(20).toInt()
//        val remainder = percentage.rem(20).toInt()
//
//        Timber.tag("StepProgressBar").d("Percentage: $percentage, Division: $division, Remainder: $remainder")
//
//
//        val color = when {
//            percentage >= 80 -> ContextCompat.getColor(context, R.color.green)
//            percentage >= 60 -> ContextCompat.getColor(context, R.color.purple_light)
//            percentage >= 40 -> ContextCompat.getColor(context, R.color.black)
//            percentage >= 20 -> ContextCompat.getColor(context, R.color.green)
//            else -> ContextCompat.getColor(context, R.color.error_color)
//        }
//
//
//        holder.stageStepBar?.apply {
//            setFilledTrackToNormalShape(color)
//            setActiveThumbToNormalShape(color)
//
//
//            val tickIconDrawable = ContextCompat.getDrawable(context, R.drawable.tick_icon_one)?.mutate()
//            tickIconDrawable?.let {
//                DrawableCompat.setTint(it, color)
//                setFilledThumbToCustomDrawable(it)
//            }
//        }
//
//        if (percentage < 20f) {
//            holder.stageStepBar?.setCurrentState(null)
//        } else {
//            holder.stageStepBar?.setCurrentState(StageStepBar.State(division - 1, remainder))
//        }
//    }
//
//    override fun getItemCount(): Int = itemList.size
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var tvRemarks: TextView? = itemView.findViewById(R.id.tvRemarks)
//        var tvBranches: TextView? = itemView.findViewById(R.id.tvBranches)
//        var stageStepBar: StageStepBar? = itemView.findViewById(R.id.stageStepBar)
//    }
//}


class StepProgressBarAdapter(
    private val context: Context,
    private val itemList: ArrayList<HorizontalBarChartDataModel>
) : RecyclerView.Adapter<StepProgressBarAdapter.ViewHolder>() {

    private lateinit var binding: StepProgressBarItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = StepProgressBarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = itemList[position]
//        holder.tvRemarks?.text = item.key // Remarks text
//        holder.tvBranches?.text = " (No of Branches ${item.value?.toInt()} | ${item.percentage}) " // Display number of branches and percentage
//
//        val percentage = item.percentage ?: 0f
//        Timber.tag("Percentage").d("Current Percentage: $percentage")
//
//        // Determine completed steps based on percentage
//        val completedSteps = when (percentage) {
//            in 1f..25f -> 1 // 1 circle filled for 1% to 25%
//            in 26f..50f -> 2 // 2 circles filled for 26% to 50%
//            in 51f..75f -> 3 // 3 circles filled for 51% to 75%
//            in 76f..100f -> 4 // 4 circles filled for 76% to 100%
//            else -> 0 // No circles filled if percentage < 1%
//        }
//
//        // Set the total number of steps (5 in this case)
//        holder.stepView?.setStepsNumber(5)
//        holder.stepView?.go(completedSteps, true) // Animate to completed steps
//        Timber.tag("ValueColor").d(item.valueColor)
//
//        // Update the StepView state
//        holder.stepView?.getState()
//            ?.doneStepLineColor(Color.parseColor(item.valueColor))
//            ?.doneCircleColor(Color.parseColor(item.valueColor))
//            ?.selectedCircleColor(Color.parseColor(item.valueColor))
//
//            ?.selectedStepNumberColor(Color.WHITE)
//            ?.doneStepMarkColor(Color.WHITE)
//            ?.nextStepCircleColor(Color.LTGRAY)
//            ?.commit()
//
//        // Set tick marks for completed steps
//        for (step in 0 until completedSteps) {
//            holder.stepView?.getState()
//                ?.doneStepMarkColor(Color.WHITE) // Set tick mark color inside completed circles
//                ?.commit()
//        }
//
//        // Optionally handle step clicks (if needed)
//        holder.stepView?.setOnStepClickListener { step ->
//            // Handle step click event if required
//            Timber.tag("StepClick").d("Step clicked: $step")
//        }
//    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.tvRemarks?.text = item.key // Set remarks text
        holder.tvBranches?.text = " (No of Branches ${item.value?.toInt()} | ${item.percentage}) " // Display number of branches and percentage

        // Get the percentage and calculate completed steps
        val percentage = item.percentage ?: 0f
        Timber.tag("Percentage").d("Current Percentage: $percentage")

        // Calculate the number of completed steps based on percentage
        val completedSteps = when {
            percentage in 0.1f..25.0f -> 1 // 1 circle filled for 1% to 25%
            percentage in 25.1f..50.0f -> 2 // 2 circles filled for 26% to 50%
            percentage in 50.1f..75.0f -> 3 // 3 circles filled for 51% to 75%
            percentage in 75.1f..100f -> 4 // 4 circles filled for 76% to 100%
            else -> 0 // No circles filled if percentage < 1%
        }

        // Set the number of steps and animate the progress
        holder.stepView?.setStepsNumber(5)
        holder.stepView?.go(completedSteps, true)
        Timber.tag("gogogo").d(position.toString())

        // Log the color
        Timber.tag("ValueColor").d(item.valueColor)

        // Update the StepView state
        holder.stepView?.getState()
            ?.doneStepLineColor(Color.parseColor(item.valueColor))
            ?.doneCircleColor(Color.parseColor(item.valueColor))
            ?.nextStepLineColor(Color.LTGRAY)
            ?.selectedCircleColor(Color.LTGRAY)
            ?.selectedTextColor(Color.LTGRAY)
            ?.nextTextColor(Color.LTGRAY)
            ?.doneStepMarkColor(Color.LTGRAY)
            ?.doneTextColor(Color.LTGRAY)
            ?.nextStepCircleColor(Color.LTGRAY)
            ?.selectedStepNumberColor(Color.LTGRAY)
            ?.commit()
        holder.stepView?.getState()
            ?.doneStepMarkColor(Color.LTGRAY)

            ?.commit()

        // Handle step clicks (if needed)
        holder.stepView?.setOnStepClickListener { step ->
            Timber.tag("StepClick").d("Step clicked: $step")
        }
    }

    override fun getItemCount(): Int = itemList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRemarks: TextView? = itemView.findViewById(R.id.tvRemarks)
        var tvBranches: TextView? = itemView.findViewById(R.id.tvBranches)
        var stepView: StepView? = itemView.findViewById(R.id.stepView)
    }
}
