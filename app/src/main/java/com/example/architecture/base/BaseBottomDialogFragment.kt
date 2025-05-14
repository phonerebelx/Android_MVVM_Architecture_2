package com.example.architecture.base

import android.content.Context
import android.os.Bundle
import com.example.architecture.ui.activities.DockActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uhfsolutions.carlutions.progress.ProgressIndicator

open class BaseBottomDialogFragment : BottomSheetDialogFragment(), ProgressIndicator {



    protected var myDockActivity: DockActivity? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDockActivity = getDockActivity()

    }

    private fun getDockActivity(): DockActivity? {
        return myDockActivity
    }








//    override fun navigateToFragment(id: Int, args: Bundle?) {
//        if(MainActivity.navController.currentBackStackEntry?.destination?.id == id){
//            closeDrawer()
//            return
//        }
//        if (args != null) {
//            MainActivity.navController.navigate(id, args)
//            return
//        }
//        MainActivity.navController.navigate(id)
//    }





    override fun showProgressIndicator() {

    }

    override fun hideProgressIndicator() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myDockActivity = context as DockActivity
    }






}