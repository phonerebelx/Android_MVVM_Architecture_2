package com.example.meezan360.ui.fragments.dashboard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PictureDrawable
import android.graphics.drawable.VectorDrawable
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caverock.androidsvg.SVG
import me.sujanpoudel.wheelview.WheelView
import com.example.meezan360.R
import com.example.meezan360.adapter.FragmentPagerAdapter
import com.example.meezan360.adapter.TopBoxesAdapter
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.ActivityMainBinding
import com.example.meezan360.databinding.FragmentDashboardBinding
import com.example.meezan360.datamodule.local.SharedPreferencesManager
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.Kpi
import com.example.meezan360.model.SearchFilterModel.GetSetFilterModel.GetSetFilterDataResponseModel
import com.example.meezan360.model.dashboardByKpi.DataModel
import com.example.meezan360.model.dashboardByKpi.FooterModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.progress.ProgressDialog
import com.example.meezan360.ui.activities.DockActivity
import com.example.meezan360.ui.activities.LoginScreen
import com.example.meezan360.ui.activities.MainFragActivity
import com.example.meezan360.ui.activities.ReportLevel1Activity
import com.example.meezan360.ui.activities.ReportLevel2Activity
import com.example.meezan360.ui.dialog.CardLevelDialog.CardLevelDialogFragment
import com.example.meezan360.ui.dialog.FingerprintPermissionDialog.FingerprintPermissionDialogFragment
import com.example.meezan360.ui.fragments.BarChartFragment
import com.example.meezan360.ui.fragments.HalfPieFragment
import com.example.meezan360.ui.fragments.HorizontalBarFragment
import com.example.meezan360.ui.fragments.InvertedBarChartFragment
import com.example.meezan360.ui.fragments.LineChartFragment
import com.example.meezan360.ui.fragments.Pie1HorizontalBar1Fragment
import com.example.meezan360.ui.fragments.Pie2Bar2Fragment
import com.example.meezan360.ui.fragments.Pie4ChartFragment
import com.example.meezan360.ui.fragments.PieChartFragment
import com.example.meezan360.ui.fragments.StackChartFragment
import com.example.meezan360.ui.fragments.StepProgressBarFragment
import com.example.meezan360.ui.fragments.TierChartFragment
import com.example.meezan360.utils.Constants
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.DashboardViewModel
import com.example.meezan360.viewmodel.SearchFragViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.sujanpoudel.wheelview.ImageArc
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.Math.abs
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.min

class DashboardFragment :
    BaseDockFragment(),
    OnChartValueSelectedListener,
    View.OnClickListener,
    OnTypeItemClickListener {
    private var kpiId: Int? = 0
    private var kpi: ArrayList<Kpi>? = null
    private lateinit var iconsList: List<Pair<Int, String>>
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var colors: List<Int>
    private var resetPassJob: Job? = null
    private lateinit var progressBarDialog: ProgressDialog
    private var lastSelectedSliceIndex: Int = -1 // Keep track of the selected slice index
    private var onrTimeUpdateIndex: Int = -1
    private var currentIndex: Int = -1
    private lateinit var pieDataSet: PieDataSet
    private var icons: MutableMap<Int, String> = mutableMapOf()
    private lateinit var iconsData: ArrayList<Int>
    private var pieChartAngleDegree: HashMap<String, Float> = HashMap<String, Float>()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var getSetFilterModel: GetSetFilterDataResponseModel

    //for top header
    private lateinit var adapter: TopBoxesAdapter
    private var centerText = ""

    //for bottom footer
    private var viewPagerAdapter: FragmentPagerAdapter? = null

    private val myViewModel: DashboardViewModel by viewModel()
    private val myViewModel2: SearchFragViewModel by viewModel()

    private var tagName: String = Constants.general

    private var footerData: List<FooterModel>? = null

    lateinit var executor: Executor
    lateinit var bmPrompt: BiometricPrompt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        val sharedPreferences =
            myDockActivity?.getSharedPreferences("Meezan360", Context.MODE_PRIVATE)
        sharedPreferencesManager = sharedPreferences?.let { SharedPreferencesManager(it) }!!

        // When app launch for first time
        if (sharedPreferencesManager.get<Boolean>(Constants.IS_FINGERPRINT) != null && sharedPreferencesManager.get<Boolean>(Constants.IS_FINGERPRINT) == false) {
            val fingerprintDialog = FingerprintPermissionDialogFragment(this)
            fingerprintDialog.show(childFragmentManager, "FingerprintPermissionDialogFragment")
        }


        pieChartAngleDegree["Deposit"] = 254.03897F
        pieChartAngleDegree["Cross Sell"] = 216.70924F
        pieChartAngleDegree["Profitibility"] = 182.16922F
        pieChartAngleDegree["Controls"] = 146.08641F
        pieChartAngleDegree["Premium"] = 109.59214F
        pieChartAngleDegree["Cash"] = 75.48029F
        pieChartAngleDegree["ADC"] = 38.358177F
        pieChartAngleDegree["Wealth"] = 1.253729F
        pieChartAngleDegree["Compliance"] = 327.46887F
        pieChartAngleDegree["Advances"] = 289.75912F
        myViewModel.viewModelScope.launch {
            myViewModel.checkVersioning()
        }
        myViewModel2.viewModelScope.launch {
            myViewModel2.getSetFilter()
        }
        handleAPIResponse()

        binding.pieChart.setOnChartValueSelectedListener(this)
        binding.btnPEDeposit.setOnClickListener(this)
        binding.btnAVGDeposit.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)
        setOnClickListener()
        return binding.root
    }


    private fun footerSetupUp(footerData: List<FooterModel>?, defaultDeposit: Int) {

        var footerList = listOf<DataModel>()
        if (footerData != null) {

            if (footerData.size >= defaultDeposit + 1) {
                footerList = footerData.get(defaultDeposit)?.dataModel!!
            }

            val fragmentsList: ArrayList<Fragment> = arrayListOf()

            footerList?.forEachIndexed { index, footer ->
                //to access fragments by passing cardType to Enum
                Timber.tag("footerList[index].cardType").d(footerList[index].cardType)
                when (footerList[index].cardType) {
                    "pie_chart" -> fragmentsList.add(
                        PieChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

                    "4pie_chart" -> fragmentsList.add(
                        Pie4ChartFragment(
                            kpiId, tagName, footerList[index]
                        )
                    )

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
            viewPagerAdapter = FragmentPagerAdapter(childFragmentManager, lifecycle)
            viewPagerAdapter?.setFragmentsForItem("", fragmentsList)
            binding.viewpager.adapter = viewPagerAdapter

        } else {
            myDockActivity?.showErrorMessage(myDockActivity!!, "Footer Data is Empty")
        }
    }


    private fun setupHeader(selectedKpiIndex: Int?, tag: String) {

        myViewModel.viewModelScope.launch {
            myViewModel.getDashboardByKpi(
                selectedKpiIndex.toString(),
                tag
            )
        }
    }


    override fun onNothingSelected() {

        binding.pieChart.onTouchListener?.setLastHighlighted(null)
        binding.pieChart.highlightValues(null)
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {


        val newColor = Color.parseColor("#765CB4")
        currentIndex = h?.x?.toInt() ?: -1

        if (lastSelectedSliceIndex == currentIndex) {
            lastSelectedSliceIndex -= 1
        }
        if (currentIndex != lastSelectedSliceIndex) {


            if (lastSelectedSliceIndex != -1) {
                val colors = binding.pieChart.data.getDataSetByIndex(h!!.dataSetIndex).colors
                colors[lastSelectedSliceIndex] = Color.parseColor("#E0E0E0")

                // Reset icon tint for the last selected slice
                val lastSelectedEntry = binding.pieChart.data.getDataSetByIndex(h.dataSetIndex)
                    .getEntryForIndex(lastSelectedSliceIndex)
                lastSelectedEntry.icon?.colorFilter = null
            }
            val colors = binding.pieChart.data.getDataSetByIndex(h!!.dataSetIndex).colors
            colors[currentIndex] = newColor
            val selectedEntry = binding.pieChart.data.getDataSetByIndex(h.dataSetIndex)
                .getEntryForIndex(currentIndex)
            selectedEntry.icon?.colorFilter =
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

            val mCenterText = iconsList[currentIndex].second
            centerText = mCenterText
            binding.centerTextView.text = mCenterText
            kpiId = kpi?.get(currentIndex)?.kpiId
            tagName = if (kpiId == 1) {
                Constants.peDeposit
            } else {
                Constants.general
            }
            setupHeader(kpiId, tagName)

            if (kpiId == 1) {
                binding.btnPEDeposit.visibility = View.VISIBLE
                binding.btnAVGDeposit.visibility = View.VISIBLE
            } else {
                binding.btnPEDeposit.visibility = View.GONE
                binding.btnAVGDeposit.visibility = View.GONE

            }


            lastSelectedSliceIndex = currentIndex // Update the last selected slice index
            binding.pieChart.invalidate() // Refresh the chart

            pieChartAngleDegree[mCenterText]?.let {
                binding.pieChart.spin(
                    500, binding.pieChart.rotationAngle,
                    it, Easing.EaseInOutCubic
                )
            }


        }

    }


    private fun showPieChart(kpi: ArrayList<Kpi>?) {

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
                icons[iconsData[kpi[i].kpiId - 1]] = kpi[i].name

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
            val entry = PieEntry(
                100f, ContextCompat.getDrawable(requireContext(), icon.key)
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
            holeRadius = 60f
            setCenterTextColor(Color.parseColor("#765CB4"))
            data = PieData(pieDataSet)
            highlightValue(
                selectedKpiIndex.toFloat(), selectedKpiIndex
            )
            invalidate()
        }

    }

    private fun initWheel(kpi: ArrayList<Kpi>?) {
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
                icons[iconsData[kpi[i].kpiId - 1]] = kpi[i].name

                //for default key
                if (kpi[i].isDefault.toString() == "true") {
                    selectedKpiIndex = i
                }

            }
        }


        //Abdul Ali Grab the isDefault kpi from icon map and insert into 0 index of icon map

        val iconPlacementList = icons.entries.toMutableList()
        if (selectedKpiIndex in iconPlacementList.indices) {

            val selectedEntry = iconPlacementList.removeAt(selectedKpiIndex)
            iconPlacementList.add(0, selectedEntry)
            icons.clear()
            iconPlacementList.forEach { icons[it.key] = it.value }
        }


        iconsList = icons.toList()


        // Abdul Ali :: Load each drawable as a Bitmap or PictureDrawable if it's SVG
        val drawables = icons.keys.map { drawableId ->
            val drawable = ContextCompat.getDrawable(requireContext(), drawableId)

            if (drawable is VectorDrawable) {
                drawable
            } else {
                try {
                    val svg = SVG.getFromResource(resources, drawableId)
                    val pictureDrawable = PictureDrawable(svg.renderToPicture())
                    pictureDrawable
                } catch (e: Exception) {
                    null
                }
            }
        }
        setCenterText(0)
        setupHeaderAndFooter(0)
        binding.wheelView.apply {
            titles = icons.values.map { it }
            arcs = arcs.mapIndexed { index, arc ->
                arc.copy(image = drawables.getOrNull(index)?.toBitmap())
            }
            refresh()
        }

        binding.wheelView.selectListener = {
            setCenterText(it)
            setupHeaderAndFooter(it)

        }
    }

    private fun setupHeaderAndFooter(index: Int) {
        kpiId = kpi?.get(index)?.kpiId
        tagName = if (kpiId == 1) {
            Constants.peDeposit
        } else {
            Constants.general
        }
        setupHeader(kpiId, tagName)

        if (kpiId == 1) {
            binding.btnPEDeposit.visibility = View.VISIBLE
            binding.btnAVGDeposit.visibility = View.VISIBLE
        } else {
            binding.btnPEDeposit.visibility = View.GONE
            binding.btnAVGDeposit.visibility = View.GONE

        }
    }

    private fun setCenterText(index: Int) {
        val mCenterText = iconsList[index].second
        centerText = mCenterText
        binding.centerTextView.text = mCenterText
    }
    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.btnPEDeposit -> {
                binding.btnPEDeposit.setBackgroundResource(R.drawable.custom_button_purple_gradient)
                binding.btnAVGDeposit.setBackgroundResource(R.drawable.custom_button_grey_gradient)
                if (kpiId == 1) tagName = Constants.peDeposit
                if (kpiId == 1) setupHeader(kpiId, tagName)
//                footerSetupUp(footerData, 0)
            }

            R.id.btnAVGDeposit -> {
                binding.btnAVGDeposit.setBackgroundResource(R.drawable.custom_button_purple_gradient)
                binding.btnPEDeposit.setBackgroundResource(R.drawable.custom_button_grey_gradient)
                if (kpiId == 1) tagName = Constants.avgDeposit

                if (kpiId == 1) setupHeader(kpiId, tagName)
            }

            R.id.ivSearch -> {

            }
        }
    }

    private fun setOnClickListener() {
        binding.let {
            it.ivSearch.setOnClickListener {
                val intent = Intent(requireContext(), MainFragActivity::class.java)
                startActivity(intent)
            }
            it.ivLogout.setOnClickListener {
                sharedPreferencesManager.logout()
                val intent = Intent(requireContext(), LoginScreen::class.java)
                startActivity(intent)
            }
            it.lvCenterView.setOnClickListener {
                if (kpiId == 1) {

                    val intent = Intent(requireContext(), ReportLevel1Activity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(requireContext(), ReportLevel2Activity::class.java)
                    intent.putExtra("kpiId", kpiId.toString())
                    intent.putExtra("kpiName", centerText)
                    intent.putExtra("identifierType", "")
                    intent.putExtra("identifier", "")
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleAPIResponse() {
        resetPassJob = lifecycleScope.launch {
            myDockActivity?.hideProgressIndicator()


            myViewModel.checkVersioning.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        myDockActivity?.hideProgressIndicator()
                        myDockActivity?.handleErrorResponse(myDockActivity!!, it)

                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {
                        myDockActivity?.hideProgressIndicator()
                        kpi = it.data?.body()?.kpis?.let { it1 -> ArrayList(it1) }
                        initWheel(kpi)
//                        showPieChart(kpi)
                    }


                }
            }
        }

        resetPassJob = lifecycleScope.launch {
            myDockActivity?.hideProgressIndicator()
            myViewModel.dashboardByKPI.collect {
                when (it) {
                    is ResponseModel.Error -> {
                        myDockActivity?.hideProgressIndicator()
                        myDockActivity?.handleErrorResponse(myDockActivity!!, it)

                    }

                    is ResponseModel.Idle -> {}
                    is ResponseModel.Loading -> {}
                    is ResponseModel.Success -> {
                        myDockActivity?.hideProgressIndicator()

                        val topBoxesData = it.data?.body()?.topBoxes
                        binding.recyclerView.layoutManager = LinearLayoutManager(
                            requireContext(), LinearLayoutManager.HORIZONTAL, false
                        )
                        adapter = TopBoxesAdapter(requireContext(), topBoxesData)
                        binding.recyclerView.adapter = adapter
                        footerData = it.data?.body()?.footer
                        if (tagName == Constants.peDeposit || tagName == Constants.general) {
                            footerSetupUp(footerData, 0)
                        } else {
                            footerSetupUp(footerData, 1)
                        }
                    }
                }
            }

        }
        resetPassJob = lifecycleScope.launch {

            myViewModel2.getSetFilterResponse.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as DockActivity).handleErrorResponse(
                            myDockActivity!!,
                            it
                        )
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {
                        getSetFilterModel = it.data?.body()!!
                        binding.centerTextDateView.text = getSetFilterModel.selected_date
                    }


                }
            }

        }
    }

    override fun onStop() {
        super.onStop()


//        resetPassJob?.cancel()
//        myViewModel.checkVersioning.value = ResponseModel.Idle("Idle State")
//        myViewModel.dashboardByKPI.value = ResponseModel.Idle("Idle State")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initFingerprint() {

        executor = Executors.newSingleThreadExecutor()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
            requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
        ) {
            bmPrompt = BiometricPrompt.Builder(requireContext())
                .setTitle("Fingerprint Authentication")
                .setDescription("Please scan your fingerprint")
                .setNegativeButton(
                    "Cancel", executor
                ) { _, _ ->
                    myDockActivity?.runOnUiThread {
                        sharedPreferencesManager.put(false, Constants.IS_FINGERPRINT)
                    }
                }.build()
        }

        if (bmPrompt != null) {
            bmPrompt.authenticate(
                CancellationSignal(),
                executor,
                @RequiresApi(Build.VERSION_CODES.P)
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        myDockActivity?.runOnUiThread {
                            Log.i("xxResult_Failed", "Authentication Failed!")
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        myDockActivity?.runOnUiThread {
                            Log.i("xxResult_Success", result.toString())
                            myDockActivity!!.showSuccessMessage(
                                myDockActivity!!,
                                "Fingerprint Enabled Successfully"
                            )
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
//                        Log.d(TAG, "onAuthenticationError -> $errorCode :: $errString")
                        if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED) {
                            setFingerprintDisabled()
                        }
                    }
                }
            )
        }

    }

    private fun setFingerprintDisabled() {
        Handler(Looper.getMainLooper()).postDelayed({
            sharedPreferencesManager.put(false, Constants.IS_FINGERPRINT)
        }, 0)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun <T> onClick(type: String, item: T, position: Int, checked: Boolean?) {

        when (type) {
            "From_Fingerprint_Dialog" -> {
                if (checked == true) {
                    sharedPreferencesManager.put(true, Constants.IS_FINGERPRINT)
                    initFingerprint()
                }
            }
        }
    }


}