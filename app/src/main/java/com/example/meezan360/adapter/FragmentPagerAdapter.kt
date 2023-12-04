package com.example.meezan360.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.meezan360.fragments.DepositComposition
import com.example.meezan360.fragments.DepositCompositionTD
import com.example.meezan360.fragments.TargetVsAchievementFragment

class FragmentPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var itemCount = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setItemCount(count: Int) {
        itemCount = count
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> DepositComposition()
            1 -> DepositCompositionTD()
            2 -> TargetVsAchievementFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}