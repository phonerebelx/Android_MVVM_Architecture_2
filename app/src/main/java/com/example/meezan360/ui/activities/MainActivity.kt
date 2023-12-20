package com.example.meezan360.ui.activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.adapter.MyAdapter
import com.example.meezan360.R
import com.example.meezan360.adapter.FragmentPagerAdapter
import com.example.meezan360.databinding.ActivityMainBinding
import com.example.meezan360.model.Kpi
import com.example.meezan360.model.dashboardByKpi.FooterModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.fragments.CustomerDepositFragment
import com.example.meezan360.ui.fragments.Pie2Bar2Fragment
import com.example.meezan360.ui.fragments.DepositCompositionTD
import com.example.meezan360.ui.fragments.DepositTrendFragment
import com.example.meezan360.ui.fragments.MoMTargetVsAchievementFragment
import com.example.meezan360.ui.fragments.MonthlyReportFragment
import com.example.meezan360.ui.fragments.OnOffBranchesFragment
import com.example.meezan360.ui.fragments.ProductWiseChartFragment
import com.example.meezan360.ui.fragments.TargetVsAchievementFragment
import com.example.meezan360.ui.fragments.TierWiseDepositFragment
import com.example.meezan360.ui.fragments.TopBottomBranches
import com.example.meezan360.utils.CardMapToFragment
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnChartValueSelectedListener, OnClickListener {

    private var kpi: List<Kpi>? = null
    private lateinit var iconsList: List<Pair<Int, String>>
    private lateinit var binding: ActivityMainBinding
    private lateinit var colors: List<Int>

    private var lastSelectedSliceIndex: Int = -1 // Keep track of the selected slice index
    private var currentIndex: Int = -1
    private lateinit var pieDataSet: PieDataSet
    private var icons: MutableMap<Int, String> = mutableMapOf()
    private lateinit var iconsData: ArrayList<Int>

    //for top header
    private lateinit var adapter: MyAdapter

    //for bottom footer
    private var viewPagerAdapter: FragmentPagerAdapter? = null

    private val myViewModel: DashboardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myViewModel.viewModelScope.launch {
            myViewModel.checkVersioning()
        }

        handleAPIResponse()

        binding.pieChart.setOnChartValueSelectedListener(this)
        binding.btnPEDeposit.setOnClickListener(this)
        binding.btnAVGDeposit.setOnClickListener(this)


    }

    private fun footerSetupUp(item: String, footerData: List<FooterModel>?, defaultDeposit: Int) {

        val cardTypeList: ArrayList<String> = arrayListOf()
        val footerList = footerData?.get(defaultDeposit)?.data
        footerList?.forEachIndexed { index, footer ->
            cardTypeList.add(footerList[index].card_type)
        }

//        CardMapToFragment.getFragment(cardTypeList[0])

        val fragmentsList: List<Fragment> = when (item) {
            "Deposit" -> listOf(
                CardMapToFragment.`2pie_2bar`.fragment,
                CardMapToFragment.DepositCompositionTD.fragment
            )

            "Cross Sell" -> listOf(
                Pie2Bar2Fragment(), TargetVsAchievementFragment(), DepositCompositionTD()
            )

            "Profitability" -> listOf(
                MoMTargetVsAchievementFragment()
            )

            "Controls" -> listOf(
                OnOffBranchesFragment()
            )

            "Premium" -> listOf(
                ProductWiseChartFragment()
            )

            "Cash" -> listOf(
                CustomerDepositFragment()
            )

            "ADC" -> listOf(
                TopBottomBranches()
            )

            "Wealth" -> listOf(
                DepositTrendFragment()
            )

            "Compliance" -> listOf(
                MonthlyReportFragment()
            )

            "Advances" -> listOf(
                TierWiseDepositFragment()
            )

            else -> emptyList() // Default empty list if no match found
        }

        viewPagerAdapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter?.setFragmentsForItem(item, fragmentsList)
        binding.viewpager.adapter = viewPagerAdapter

    }

    private fun setupHeader(selectedKpiIndex: Int?) {
        myViewModel.viewModelScope.launch { myViewModel.getDashboardByKpi(selectedKpiIndex.toString()) }
    }


    override fun onNothingSelected() {
        //pie chart nothing selected
        binding.pieChart.onTouchListener?.setLastHighlighted(null)
        binding.pieChart.highlightValues(null)

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

        val newColor = Color.parseColor("#765CB4")
        currentIndex = h?.x?.toInt() ?: -1 // Retrieve the selected slice index

        if (currentIndex != lastSelectedSliceIndex) {
            // Reset color for the last selected slice
            if (lastSelectedSliceIndex != -1) {
                val colors = binding.pieChart.data.getDataSetByIndex(h!!.dataSetIndex).colors
                colors[lastSelectedSliceIndex] = Color.parseColor("#E0E0E0")

                // Reset icon tint for the last selected slice
                val lastSelectedEntry = binding.pieChart.data.getDataSetByIndex(h.dataSetIndex)
                    .getEntryForIndex(lastSelectedSliceIndex)
                lastSelectedEntry.icon?.colorFilter = null
            }
            // Change color for the newly selected slice
            val colors = binding.pieChart.data.getDataSetByIndex(h!!.dataSetIndex).colors
            colors[currentIndex] = newColor

            // Tint the selected icon white
            val selectedEntry = binding.pieChart.data.getDataSetByIndex(h.dataSetIndex)
                .getEntryForIndex(currentIndex)
            selectedEntry.icon?.colorFilter =
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

            // Update the center text based on the selected entry
            val mCenterText = iconsList[currentIndex].second
            binding.pieChart.centerText = mCenterText

            setupHeader(kpi?.get(currentIndex)?.kpi_id)
//            footerSetupUp(mCenterText, footerData)

            lastSelectedSliceIndex = currentIndex // Update the last selected slice index
            binding.pieChart.invalidate() // Refresh the chart
        }
    }


    private fun showPieChart(kpi: List<Kpi>?) {

        iconsData = arrayListOf(
            R.drawable.deposit_icon,
            R.drawable.cross_sell_icon,
            R.drawable.profitability,
            R.drawable.controls_icon,
            R.drawable.premium_icon,
            R.drawable.cash_icon,
            R.drawable.adc_icon,
            R.drawable.wealth_icon,
            R.drawable.compliance_icon,
            R.drawable.advances_icon
        )
        var selectedKpiIndex = 0
        if (kpi != null) {
            for (i in kpi.indices) {
                icons[iconsData[i]] = kpi[i].name

                //for default key
                if (kpi[i].is_default.toString() == "true") {
                    selectedKpiIndex = i
                }

            }
        }

        iconsList = icons.toList()

        // Initializing colors for the entries
        colors = List(icons.size) { Color.parseColor("#E0E0E0") }

        // Input data and fit data into pie chart entry
        val pieEntries = mutableListOf<PieEntry>()

        for (icon in icons) {
            val entry = PieEntry(
                100f, ContextCompat.getDrawable(this, icon.key)
            )
            pieEntries.add(entry)
        }


        // Collecting the entries with label name
        pieDataSet = PieDataSet(pieEntries, "")
        // Setting text size of the value (hide text values)
        pieDataSet.valueTextSize = 0f
        // Providing color list for coloring different entries
        pieDataSet.colors = colors
        pieDataSet.selectionShift = 0f


        binding.pieChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            setCenterTextSize(20f)
            holeRadius = 60f //to fix the white border of center of circle
            setCenterTextColor(Color.parseColor("#765CB4"))
            data = PieData(pieDataSet)
            highlightValue(
                selectedKpiIndex.toFloat(),
                selectedKpiIndex
            ) //Select First element by default i.e. Deposit
            invalidate()
        }

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnPEDeposit -> {
                binding.btnPEDeposit.setBackgroundResource(R.drawable.custom_button_purple_gradient)
                binding.btnAVGDeposit.setBackgroundResource(R.drawable.custom_button_grey_gradient)
            }

            R.id.btnAVGDeposit -> {
                binding.btnAVGDeposit.setBackgroundResource(R.drawable.custom_button_purple_gradient)
                binding.btnPEDeposit.setBackgroundResource(R.drawable.custom_button_grey_gradient)

            }
        }
    }

    private fun handleAPIResponse() {
        lifecycleScope.launchWhenStarted {
            myViewModel.checkVersioning.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            this@MainActivity,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> Toast.makeText(
                        this@MainActivity,
                        "Loading..",
                        Toast.LENGTH_SHORT
                    ).show()

                    is ResponseModel.Success -> {

                        kpi = it.data?.body()?.kpis
                        showPieChart(kpi)
                    }
                }
            }
        }

        lifecycleScope.launch {
            myViewModel.dashboardByKPI.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        Toast.makeText(
                            this@MainActivity,
                            "error: " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Loading.. ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Success -> {

                        //for header
                        val topBoxesData = it.data?.body()?.top_boxes
                        binding.recyclerView.layoutManager =
                            LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        adapter = MyAdapter(topBoxesData)
                        binding.recyclerView.adapter = adapter

                        //for footer
                        val footerData = it.data?.body()?.footer

//                        footerSetupUp("Deposit",footerData)
                    }
                }
            }
        }

    }


}