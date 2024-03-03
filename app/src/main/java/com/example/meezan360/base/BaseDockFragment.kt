package com.example.meezan360.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.ui.activities.DockActivity



abstract class BaseDockFragment : Fragment() {

    protected var myDockActivity: DockActivity? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDockActivity = getDockActivity()
    }

    private fun getDockActivity(): DockActivity? {
        return myDockActivity
    }





    override fun onAttach(context: Context) {
        super.onAttach(context)
        myDockActivity = context as DockActivity
    }


}