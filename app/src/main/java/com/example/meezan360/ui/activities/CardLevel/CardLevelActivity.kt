package com.example.meezan360.ui.activities.CardLevel

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.CardAdapter.CardLevelAdapter
import com.example.meezan360.adapter.DepositFooterAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.adapter.levelTwoAdapter.TopMenuAdapter
import com.example.meezan360.databinding.ActivityCardLevel2Binding
import com.example.meezan360.interfaces.OnItemClickListener
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.dashboardByKpi.TopBoxesModel
import com.example.meezan360.model.reports.Level2ReportModel
import com.example.meezan360.model.reports.cards.Card
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.dialog.CardLevelDialog.CardLevelDialogFragment
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CardLevelActivity : DockActivity(), OnItemClickListener, OnTypeItemClickListener {
    private lateinit var binding: ActivityCardLevel2Binding
    private val myViewModel: ReportViewModel by viewModel()
    private var responseBody: ArrayList<Level2ReportModel>? = arrayListOf()
    private lateinit var topBoxesAdapter: TopBoxesAdapter
    private lateinit var cardParentAdapter: CardLevelAdapter
    private lateinit var footerAdapter: DepositFooterAdapter
    var kpiName: String? = null
    private lateinit var topMenuAdapter: TopMenuAdapter
    var kpiId: String? = null
    private var tableId: String = "0"
    private var identifierType: String = ""
    private var identifier: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardLevel2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMainFrag.toolbar)
        supportActionBar?.title = ""
        binding.tbMainFrag.toolbarTitle.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.purple_dark
            )
        )
        val extras = intent.extras
        if (extras != null) {
            kpiId = extras.getString("kpiId")
            kpiName = extras.getString("kpiName")
            tableId = extras.getString("tableId").toString()
        }
        binding.tbMainFrag.toolbarTitle.text = kpiName
        myViewModel.viewModelScope.launch {
            showProgressIndicator()
            kpiId?.let { myViewModel.getLevelTwo(it, tableId, identifierType, identifier) }
        }
        binding.tbMainFrag.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        handleAPIResponse()

    }

    private fun setupTopMenu(topBoxes: ArrayList<String>) {

        binding.recyclerViewTopCategory.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        topMenuAdapter = TopMenuAdapter(this, topBoxes, this)
        binding.recyclerViewTopCategory.adapter = topMenuAdapter
    }

    private fun setupTopBoxes(topBoxes: ArrayList<TopBoxesModel>?) {

        binding.recyclerViewTopBox.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        topBoxesAdapter = TopBoxesAdapter(this, topBoxes)
        binding.recyclerViewTopBox.adapter = topBoxesAdapter
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.levelTwo.collect {
                hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        handleErrorResponse(it)
                        if (it.data?.code() == 400 &&  it.data?.message() == "Bad Request"){
                            onBackPressed()
                        }
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {}

                    is ResponseModel.Success -> {
                        if (responseBody != null) {


                            val topMenuList = ArrayList<String>()
                            responseBody = it.data?.body()
//                            binding.tbMainFrag.toolbarTitle.text = responseBody!!.get(0).table.get(0).table_title
                            for (i in responseBody!!) {
                                i.topMenu?.let { it1 -> topMenuList.add(it1) }
                            }

                            setupTopMenu(topMenuList)
                            setupTopBoxes(responseBody?.get(0)?.boxes)
                            responseBody?.get(0)?.table?.get(0)?.card?.let { it1 ->
                                setupCardRecyclerView(
                                    it1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupCardRecyclerView(cardList: ArrayList<Card>) {

        binding.recyclerViewReport.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cardParentAdapter = CardLevelAdapter(this, this)
        cardParentAdapter.setList(cardList)
        binding.recyclerViewReport.adapter = cardParentAdapter
    }

    override fun onClick(item: String?, position: Int, checked: Boolean?) {
//        binding.tbMainFrag.toolbarTitle.text = responseBody!!.get(position).table.get(0).table_title
        setupTopBoxes(responseBody?.get(position)?.boxes)
        setupCardRecyclerView(responseBody?.get(position)?.table!![0].card)
    }

    override fun <T> onClick(type: String, item: T, position: Int, checked: Boolean?) {
        val getItem = item as Card

        when (type) {
            "On_Card_Item_Click" -> {
                val cardLevelDialogFragment = CardLevelDialogFragment()
                cardLevelDialogFragment.cifId = getItem.cif_id
                cardLevelDialogFragment.customerName = getItem.customer_name
                cardLevelDialogFragment.show(supportFragmentManager, "CardLevelDialogFragment")
            }
        }
    }

}