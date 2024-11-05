package com.example.meezan360.ui.dialog.CardLevelDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.CardAdapter.AccountDetailAdapter
import com.example.meezan360.adapter.CardAdapter.CardDetailAdapter
import com.example.meezan360.base.BaseDialogFragment
import com.example.meezan360.databinding.FragmentCardLevelDialogBinding
import com.example.meezan360.model.CardLevelModel.Data
import com.example.meezan360.model.CardLevelModel.GetCardLevelDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CardLevelDialogFragment() : BaseDialogFragment() {

    lateinit var binding: FragmentCardLevelDialogBinding
    private var responseBody: GetCardLevelDataModel? = null
    private lateinit var accountAdapter : AccountDetailAdapter
    private lateinit var cardDetailAdapter: CardDetailAdapter
    lateinit var cifId: String
    lateinit var customerName: String
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
        setData()
        handleAPIResponse()
        return binding.root
    }


    private fun setData() {
        binding.tvCustomer.text = customerName
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.customerService.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        myDockActivity?.hideProgressIndicator()
                        (requireActivity() as DockActivity).handleErrorResponse(myDockActivity!!,it)

                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        myDockActivity?.hideProgressIndicator()
                        Log.d("it.data?.body()", it.data?.body().toString())
                        responseBody = it.data?.body()

                        responseBody?.let {
                            setCardDetailAdapter(it)
                            setCardAdapter(it)


                        }
                    }
                }
            }
        }
    }

    private fun setCardAdapter(cardLevelDataModel: GetCardLevelDataModel) {
//        val accountNumbers = (cardLevelDataModel.find { it.key == "Account No." }?.value as? Any) as? List<String> ?: emptyList()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        accountAdapter = AccountDetailAdapter(requireContext())
        accountAdapter.setList(cardLevelDataModel.account)
        binding.recyclerView.adapter = accountAdapter
    }

    private fun setCardDetailAdapter(cardLevelDataModel: GetCardLevelDataModel) {
        binding.rvDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        cardDetailAdapter = CardDetailAdapter(requireContext())
        cardDetailAdapter.setList(cardLevelDataModel.data)
        binding.rvDetail.adapter = cardDetailAdapter
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