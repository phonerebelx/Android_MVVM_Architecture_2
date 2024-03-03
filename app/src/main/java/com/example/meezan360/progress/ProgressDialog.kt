package com.example.meezan360.progress

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.meezan360.R

class ProgressDialog: DialogFragment() {

    override fun onStart() {
        super.onStart()
        val d: Dialog = dialog!!
        if (d.window != null) d.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = layoutInflater
        val rootView: View = inflater.inflate(R.layout.progress, null)
        isCancelable = false
        return AlertDialog.Builder(context).setView(rootView).create()
    }

    fun showDialog(manager: FragmentManager?, tag: String?) {
        super.show(manager!!, tag)
    }
}