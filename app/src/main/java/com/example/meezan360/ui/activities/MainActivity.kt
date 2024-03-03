package com.example.meezan360.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meezan360.R
import com.example.meezan360.adapter.FragmentPagerAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.databinding.ActivityMainBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.model.Kpi
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.dashboardByKpi.FooterModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.progress.ProgressDialog
import com.example.meezan360.ui.fragments.BarChartFragment
import com.example.meezan360.ui.fragments.HalfPieFragment
import com.example.meezan360.ui.fragments.HorizontalBarFragment
import com.example.meezan360.ui.fragments.InvertedBarChartFragment
import com.example.meezan360.ui.fragments.LineChartFragment
import com.example.meezan360.ui.fragments.Pie1HorizontalBar1Fragment
import com.example.meezan360.ui.fragments.Pie2Bar2Fragment
import com.example.meezan360.ui.fragments.StackChartFragment
import com.example.meezan360.ui.fragments.StepProgressBarFragment
import com.example.meezan360.ui.fragments.TierChartFragment
import com.example.meezan360.utils.Constants
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.uhfsolutions.carlutions.progress.ProgressIndicator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : DockActivity(), OnChartValueSelectedListener, OnClickListener{


    val KEY_FRAG_FIRST = "firstFrag"
    private var kpiId: Int? = 0
    private var kpi: List<Kpi>? = null
    private lateinit var iconsList: List<Pair<Int, String>>
    private lateinit var binding: ActivityMainBinding
    private lateinit var colors: List<Int>
    private lateinit var progressBarDialog: ProgressDialog
    private var lastSelectedSliceIndex: Int = -1 // Keep track of the selected slice index
    private var onrTimeUpdateIndex: Int = -1
    private var currentIndex: Int = -1
    private lateinit var pieDataSet: PieDataSet
    private var icons: MutableMap<Int, String> = mutableMapOf()
    private lateinit var iconsData: ArrayList<Int>
    private var pieChartAngleDegree: HashMap<String, Float> = HashMap<String, Float>()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    //for top header
    private lateinit var adapter: TopBoxesAdapter

    //for bottom footer
    private var viewPagerAdapter: FragmentPagerAdapter? = null

    private val myViewModel: DashboardViewModel by viewModel()

    private var tagName: String = Constants.general

    private var footerData: List<FooterModel>? = null
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = SharedPreferencesManager(sharedPreferences)
        pieChartAngleDegree["Deposit"] = 254.03897F
        pieChartAngleDegree["Cross Sell"] = 216.70924F
        pieChartAngleDegree["Profitibility"] = 182.16922F
        pieChartAngleDegree["Controls"] = 146.08641F
        pieChartAngleDegree["Premium"] = 109.59214F
        pieChartAngleDegree["Cash"] = 75.48029F
        pieChartAngleDegree["ADC"] = 38.358177F
        pieChartAngleDegree["Wealth"] = 1.253729F
        pieChartAngleDegree["Complaince"] = 327.46887F
        pieChartAngleDegree["Advances"] = 289.75912F
        myViewModel.viewModelScope.launch {
            myViewModel.checkVersioning()
        }

        handleAPIResponse()

        binding.pieChart.setOnChartValueSelectedListener(this)
        binding.btnPEDeposit.setOnClickListener(this)
        binding.btnAVGDeposit.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)

    }

    private fun footerSetupUp(footerData: List<FooterModel>?, defaultDeposit: Int) {

        footerData?.toString()?.let { Log.d("footerSetupUp: ", it) }
        var footerList = listOf<DataModel>()
        if (footerData != null) {

            if (footerData.size >= defaultDeposit + 1) {
                footerList = footerData.get(defaultDeposit)?.dataModel!!
            }

            val fragmentsList: ArrayList<Fragment> = arrayListOf()

            footerList?.forEachIndexed { index, footer ->
                //to access fragments by passing cardType to Enum
                when (footerList[index].cardType) {
                    "2pie_2bar" -> fragmentsList.add(
                        Pie2Bar2Fragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "1pie_1horizontal_bar" -> fragmentsList.add(
                        Pie1HorizontalBar1Fragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "bar_chart" -> fragmentsList.add(
                        BarChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "stack_chart", "stack_with_toggle" -> fragmentsList.add(
                        StackChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "bar_chart_single_value" -> fragmentsList.add(
                        InvertedBarChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "tier_chart" -> fragmentsList.add(
                        TierChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "half_pie" -> fragmentsList.add(
                        HalfPieFragment(
                            kpiId,
                            tagName,
                            footerList[index]
                        )
                    )

                    "2_axis_line_chart", "line_chart" -> fragmentsList.add(
                        LineChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "horizontal_bar" -> fragmentsList.add(
                        HorizontalBarFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "steper_lines" -> fragmentsList.add(
                        StepProgressBarFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                }
            }

            viewPagerAdapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)
            viewPagerAdapter?.setFragmentsForItem("", fragmentsList)
            binding.viewpager.adapter = viewPagerAdapter

        } else {
            Toast.makeText(this, "Footer Data is Empty", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setupHeader(selectedKpiIndex: Int?) {
        showProgressIndicator()
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
            kpiId = kpi?.get(currentIndex)?.kpiId
            setupHeader(kpiId)

            tagName = if (kpiId == 1) {
                Constants.peDeposit
            } else {
                Constants.general
            }

            if (kpiId == 1) {
                binding.btnPEDeposit.visibility = View.VISIBLE
                binding.btnAVGDeposit.visibility = View.VISIBLE
            } else {
                binding.btnPEDeposit.visibility = View.GONE
                binding.btnAVGDeposit.visibility = View.GONE

            }





            if (index == 0) {
                index++
                onrTimeUpdateIndex = currentIndex
            }
            val angle = if (binding.pieChart.getAngleForPoint(h.x, h.y).isNaN()) {
                270.0F
            } else {
                binding.pieChart.getAngleForPoint(h.x, h.y)
            }


            lastSelectedSliceIndex = currentIndex // Update the last selected slice index
            binding.pieChart.invalidate() // Refresh the chart


            pieChartAngleDegree[mCenterText]?.let {
                binding.pieChart.spin(
                    500, binding.pieChart.rotationAngle,
                    it, Easing.EaseInOutCubic
                )
            }


        } else {
            //same pie chart item is clicked twice
            if (kpiId == 1) {
                //for deposit
                val intent = Intent(this, ReportLevel1Activity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, ReportLevel2Activity::class.java)
                intent.putExtra("kpiId", kpiId.toString())
                intent.putExtra("identifierType", "")
                intent.putExtra("identifier", "")
                startActivity(intent)
            }
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
                if (kpi[i].isDefault.toString() == "true") {
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
            val entry = PieEntry(100f, ContextCompat.getDrawable(this, icon.key)
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
                selectedKpiIndex.toFloat(), selectedKpiIndex
            ) //Select First element by default i.e. Deposit
            invalidate()
        }

    }


    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.btnPEDeposit -> {
                binding.btnPEDeposit.setBackgroundResource(R.drawable.custom_button_purple_gradient)
                binding.btnAVGDeposit.setBackgroundResource(R.drawable.custom_button_grey_gradient)
                if (kpiId == 1) tagName = Constants.peDeposit

                footerSetupUp(footerData, 0)
            }

            R.id.btnAVGDeposit -> {
                binding.btnAVGDeposit.setBackgroundResource(R.drawable.custom_button_purple_gradient)
                binding.btnPEDeposit.setBackgroundResource(R.drawable.custom_button_grey_gradient)
                tagName = Constants.avgDeposit

                if (kpiId == 1) footerSetupUp(footerData, 1)
            }

            R.id.ivSearch -> {

                val intent = Intent(this, MainFragActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun handleAPIResponse() {
        lifecycleScope.launch {
            myViewModel.checkVersioning.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        hideProgressIndicator()
                        handleErrorResponse(it)
                        Toast.makeText(
                            this@MainActivity, "error: " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
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
                        hideProgressIndicator()
                        handleErrorResponse(it)
                        Toast.makeText(
                            this@MainActivity, "error: " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Loading.. ",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }

                    is ResponseModel.Success -> {
                        hideProgressIndicator()
                        //for header
                        val topBoxesData = it.data?.body()?.topBoxes
                        binding.recyclerView.layoutManager = LinearLayoutManager(
                            this@MainActivity, LinearLayoutManager.HORIZONTAL, false
                        )
                        adapter = TopBoxesAdapter(applicationContext, topBoxesData)
                        binding.recyclerView.adapter = adapter

                        //for footer
                        footerData = it.data?.body()?.footer

                        footerSetupUp(footerData, 0)

                    }
                }
            }
        }

    }






}