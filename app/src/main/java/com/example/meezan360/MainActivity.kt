package com.example.meezan360

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.circlemenu.adapter.MyAdapter
import com.example.circlemenu.model.DetailModel
import com.example.meezan360.adapter.FragmentPagerAdapter
import com.example.meezan360.databinding.ActivityMainBinding
import com.example.meezan360.fragments.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class MainActivity : AppCompatActivity(), OnChartValueSelectedListener, OnClickListener {

    private lateinit var iconsList: List<Pair<Int, String>>
    private lateinit var binding: ActivityMainBinding
    private lateinit var colors: List<Int>

    private var lastSelectedSliceIndex: Int = -1 // Keep track of the selected slice index
    private var currentIndex: Int = -1
    private lateinit var pieDataSet: PieDataSet
    private lateinit var icons: Map<Int, String>

    //for top header
    private lateinit var adapter: MyAdapter
    private var detailList: ArrayList<DetailModel> = arrayListOf()


    //for bottom footer
    private var viewPagerAdapter: FragmentPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pieChart.setOnChartValueSelectedListener(this)
        binding.btnPEDeposit.setOnClickListener(this)
        binding.btnAVGDeposit.setOnClickListener(this)
        showPieChart()

        footerSetupUp("Deposit")
        setupRecyclerView()

    }

    private fun footerSetupUp(item: String) {

        val fragmentsList: List<Fragment> = when (item) {
            "Deposit" -> listOf(TargetVsAchievementFragment(), DepositComposition())
            "Cross Sell" -> listOf(
                DepositComposition(),
                TargetVsAchievementFragment(),
                DepositCompositionTD()
            )
            "Profitability" -> listOf(
                MoMTargetVsAchievementFragment()
            )
            "Controls" -> listOf(
                OnOffBranchesFragment()
            )
            // Add cases for other pie chart items...
            else -> emptyList() // Default empty list if no match found
        }

        viewPagerAdapter = FragmentPagerAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter?.setFragmentsForItem(item, fragmentsList)
        binding.viewpager.adapter = viewPagerAdapter

    }

    private fun setupRecyclerView() {
        detailList.add(DetailModel("Total", "1,534", "", "BIn"))
        detailList.add(DetailModel("CASA", "659,213", "55.98%", "MIn"))
        detailList.add(DetailModel("Current", "719,715", "46.9%", "MIn"))
        detailList.add(DetailModel("Saving", "552,237", "35.98%", "MIn"))
        detailList.add(DetailModel("Term Deposit", "262,767", "17.12%", "MIn"))

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = MyAdapter(detailList)
        binding.recyclerView.adapter = adapter
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
            // Do something with the center text, e.g., display it in a TextView

            footerSetupUp(mCenterText) //

            lastSelectedSliceIndex = currentIndex // Update the last selected slice index
            binding.pieChart.invalidate() // Refresh the chart
        }
    }


    private fun showPieChart() {

        icons = mapOf(
            R.drawable.deposit_icon to "Deposit",
            R.drawable.cross_sell_icon to "Cross Sell",
            R.drawable.profitability to "Profitability",
            R.drawable.controls_icon to "Controls",
            R.drawable.premium_icon to "Premium",
            R.drawable.cash_icon to "Cash",
            R.drawable.adc_icon to "ADC",
            R.drawable.wealth_icon to "Wealth",
            R.drawable.compliance_icon to "Compliance",
            R.drawable.advances_icon to "Advances"
        )

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
            highlightValue(0f, 0) //Select First element by default i.e. Deposit
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

}