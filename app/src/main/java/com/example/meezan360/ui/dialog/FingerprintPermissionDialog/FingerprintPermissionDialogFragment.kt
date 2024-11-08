package com.example.meezan360.ui.dialog.FingerprintPermissionDialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meezan360.R
import com.example.meezan360.base.BaseBottomDialogFragment
import com.example.meezan360.databinding.FragmentFingerprintPermissionDialogBinding
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class FingerprintPermissionDialogFragment(
    val onTypeItemClickListener: OnTypeItemClickListener
) : BaseBottomDialogFragment() {


    private var _binding: FragmentFingerprintPermissionDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFingerprintPermissionDialogBinding.inflate(inflater, container, false)

        setOnClickListener()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    private fun setOnClickListener() {
        binding.apply {
            mbEnable.setOnClickListener {
                onTypeItemClickListener.onClick("From_Fingerprint_Dialog",null,checked = true)
                dialog?.dismiss()
            }
        }
    }
}