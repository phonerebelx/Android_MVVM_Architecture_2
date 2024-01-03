package com.example.meezan360.ui.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meezan360.databinding.FragmentStepProgressBarBinding


class StepProgressBarFragment : Fragment() {

    private lateinit var binding: FragmentStepProgressBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepProgressBarBinding.inflate(layoutInflater)

//        val stepsBeanList: MutableList<StepBean> = ArrayList()
//        val stepBean0 = StepBean("One", 1)
//        val stepBean1 = StepBean("Two", 1)
//        val stepBean2 = StepBean("Two", 1)
//        val stepBean3 = StepBean("Two", 0)
//        val stepBean4 = StepBean("Two", -1)
//        stepsBeanList.add(stepBean0)
//        stepsBeanList.add(stepBean1)
//        stepsBeanList.add(stepBean2)
//        stepsBeanList.add(stepBean3)
//        stepsBeanList.add(stepBean4)
//
//
//        binding.stepView
//            .setStepViewTexts(stepsBeanList)
//            .setTextSize(12)
//            .setStepsViewIndicatorCompletedLineColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.white
//                )
//            )
//            .setStepsViewIndicatorUnCompletedLineColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.holo_purple
//                )
//            )
//            .setStepViewComplectedTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.white
//                )
//            )
//            .setStepViewUnComplectedTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.holo_purple
//                )
//            )
//            .setStepsViewIndicatorCompleteIcon(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.ic_delete
//                )
//            )
//            .setStepsViewIndicatorDefaultIcon(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.ic_lock_lock
//                )
//            )
//            .setStepsViewIndicatorAttentionIcon(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.ic_menu_add
//                )
//            )


        return binding.root
    }

}