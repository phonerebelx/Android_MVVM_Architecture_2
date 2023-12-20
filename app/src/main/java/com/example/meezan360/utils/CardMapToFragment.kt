package com.example.meezan360.utils

import androidx.fragment.app.Fragment
import com.example.meezan360.ui.fragments.DepositCompositionTD
import com.example.meezan360.ui.fragments.OnOffBranchesFragment
import com.example.meezan360.ui.fragments.Pie2Bar2Fragment

enum class CardMapToFragment(var fragment: Fragment) {
    `2pie_2bar`(Pie2Bar2Fragment()),
    DepositCompositionTD(DepositCompositionTD()),
    MoMTargetVsAchievementFragment(OnOffBranchesFragment());

    companion object {
        fun getFragment(name: String): Fragment = CardMapToFragment.valueOf(name).fragment
    }
}