package com.example.meezan360.ui.dialog.CardLevelDialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.CardAdapter.AccountDetailAdapter
import com.example.meezan360.adapter.CardAdapter.CardLevelAdapter
import com.example.meezan360.base.BaseDialogFragment
import com.example.meezan360.databinding.ActivityCardLevel2Binding
import com.example.meezan360.databinding.FragmentCardLevelDialogBinding
import com.example.meezan360.model.CardLevelModel.CardLevelDataModel
import com.example.meezan360.model.reports.Level2ReportModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CardLevelDialogFragment() : BaseDialogFragment() {

    lateinit var binding: FragmentCardLevelDialogBinding
    private var responseBody: CardLevelDataModel? = null
    private lateinit var accountAdapter : AccountDetailAdapter
    lateinit var cifId: String
    private val myViewModel: ReportViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardLevelDialogBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        myViewModel.viewModelScope.launch {
            myDockActivity?.showProgressIndicator()
           myViewModel.getCustomerService(cifId)
        }

        handleAPIResponse()
        return binding.root
    }




    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.customerService.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        myDockActivity?.hideProgressIndicator()
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                        Toast.makeText(
                            requireContext(),
                            "my error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        myDockActivity?.hideProgressIndicator()

                        responseBody = it.data?.body()

                        responseBody?.let {
                            setCardAdapter(it)
                            setData(it)
                        }
                    }
                }
            }
        }
    }
    private fun setCardAdapter(cardLevelDataModel: CardLevelDataModel){

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        accountAdapter = AccountDetailAdapter(requireContext())
        accountAdapter.setList(cardLevelDataModel.account_no)
        binding.recyclerView.adapter = accountAdapter
    }

    private fun setData(cardLevelDataModel: CardLevelDataModel){
        binding.tvCifIdDesc.text = cardLevelDataModel.cif_id
        binding.tvAccDetail.text = cardLevelDataModel.no_of_accounts
        binding.tvProdDetail.text = cardLevelDataModel.no_of_products
        binding.tvProdNameDesc.text = cardLevelDataModel.product_names
    }
    override fun onResume() {
        super.onResume()

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
//        val marginTop = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp)
//        dialog?.window?.attributes?.y = marginTop

        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}