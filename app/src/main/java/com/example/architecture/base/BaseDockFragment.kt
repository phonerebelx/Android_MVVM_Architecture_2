package com.example.architecture.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.architecture.ui.activities.DockActivity



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