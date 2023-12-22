package com.example.meezan360.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fragmentsMap = HashMap<String, ArrayList<Fragment>>()

    @SuppressLint("NotifyDataSetChanged")
    fun setFragmentsForItem(item: String, fragments: ArrayList<Fragment>) {
        fragmentsMap[item] = fragments
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragmentsMap.values.firstOrNull()?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        val fragments = fragmentsMap.values.firstOrNull()
            ?: throw IllegalArgumentException("No fragments found")

        val localPosition = position % fragments.size
        return fragments[localPosition]
    }

}
