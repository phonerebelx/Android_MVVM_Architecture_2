package com.example.meezan360.ui.fragments.SearchFragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.base.BaseDockFragment
import com.example.meezan360.databinding.FragmentSearchBinding
import com.example.meezan360.model.SearchFilterModel.Branch
import com.example.meezan360.model.SearchFilterModel.GetSetFilterModel.GetSetFilterDataResponseModel
import com.example.meezan360.model.SearchFilterModel.SearchFilterDataModel
import com.example.meezan360.model.SearchFilterModel.SetFilterModel.SetFilterRequestDataModel
import com.example.meezan360.network.ResponseModel
import com.example.meezan360.ui.activities.MainActivity
import com.example.meezan360.ui.activities.MainFragActivity
import com.example.meezan360.utils.handleErrorResponse
import com.example.meezan360.viewmodel.SearchFragViewModel
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class SearchFragment : BaseDockFragment() {
    private lateinit var binding: FragmentSearchBinding
    private val myViewModel: SearchFragViewModel by viewModel()
    private lateinit var lovsModel: SearchFilterDataModel
    private lateinit var getSetFilterModel: GetSetFilterDataResponseModel
    private var regionItem: String = ""
    private var areaItem: String = ""
    private var branchItem: String = ""
    private var branchCode: String = ""
    private lateinit var branchDict: HashMap<String,String>
    private lateinit var selectedDate: String
    private lateinit var regionArray: ArrayList<String>
    private lateinit var areaArray: ArrayList<String>
    private lateinit var branchArray: ArrayList<String>
    private lateinit var regionAdapterItem: ArrayAdapter<String>
    private lateinit var areaAdapterItem: ArrayAdapter<String>
    private lateinit var branchAdapterItem: ArrayAdapter<String>


    private var indexArrayForAllLovs: ArrayList<Int> = arrayListOf(0, 0, 0)

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(layoutInflater)


        getSetFilter()
        handleAPIResponse()
        setOnClickListener()




        return binding.root
    }

    private fun showRegionArray(regionArray: ArrayList<String>, currentRegion: String = "") {


        binding.actvRegion.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            val regionIconSpinnerItems = regionArray.map { IconSpinnerItem(text = it) }
            setItems(regionIconSpinnerItems)
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)


        }


        binding.actvRegion.setOnSpinnerItemSelectedListener<IconSpinnerItem> { oldIndex, oldItem, newIndex, newItem ->

            regionItem = newItem.text.toString()
            indexArrayForAllLovs[0] = newIndex
            if (regionItem.isNotEmpty() && regionItem != "") {

                if (areaArray.isNotEmpty()) {
                    areaArray.clear()
                }
                val area = lovsModel.get(indexArrayForAllLovs[0]).area
                for (item in area) {
                    areaArray.add(item.area_name)
                }
                if (areaArray.isNotEmpty()) {
                    showAreaArray(areaArray)
                }
            }
            binding.actvRegion.clearFocus()
        }

        if (currentRegion != "") {
            regionItem = currentRegion
            binding.actvRegion.text = regionItem
            for ((index, item) in regionArray.withIndex()) {

                if (regionItem == item) {
                    indexArrayForAllLovs[0] = index
                    if (regionItem.isNotEmpty() && regionItem != "") {

                        if (areaArray.isNotEmpty()) {
                            areaArray.clear()
                        }
                        val area = lovsModel.get(indexArrayForAllLovs[0]).area
                        for (item in area) {
                            areaArray.add(item.area_name)
                        }
                        if (areaArray.isNotEmpty()) {
                            showAreaArray(areaArray, getSetFilterModel.selected_area)
                        }
                    }

                }
            }
        }
    }


    private fun showAreaArray(areaArray: ArrayList<String>, currentArea: String = "") {
        binding.actvArea.text = ""
        binding.actvBranch.text = ""



        binding.actvArea.apply {
            binding.actvRegion.dismiss()
            setSpinnerAdapter(IconSpinnerAdapter(this))
            val areaIconSpinnerItems = areaArray.map { IconSpinnerItem(text = it) }
            setItems(areaIconSpinnerItems)
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)

        }
        binding.actvArea.setOnSpinnerItemSelectedListener<IconSpinnerItem> { oldIndex, oldItem, newIndex, newItem ->

            areaItem = newItem.text.toString()

            indexArrayForAllLovs[1] = newIndex

            if (areaItem.isNotEmpty() && areaItem != "") {

                if (binding.actvRegion.text.toString() == "") {

                    for (index in lovsModel.indices) {
                        val item = lovsModel[index]
                       item.area.forEachIndexed { areaIndex, area ->
                           if (areaItem == area.area_name){

                               indexArrayForAllLovs[0] = index
                               indexArrayForAllLovs[1] = areaIndex
                           }
                       }
                    }
                }


                val branch = lovsModel.get(indexArrayForAllLovs[0]).area.get(indexArrayForAllLovs[0]).branch


                if (branchArray.isNotEmpty()) {
                    branchArray.clear()
                }

                for (item in branch) {
                    branchArray.add(item.branch_name)
                }

                if (branchArray.isNotEmpty()) {
                    showBranchArray(branchArray)
                }
            }
            binding.actvArea.clearFocus()
        }



        if (currentArea != "") {

            areaItem = currentArea
            binding.actvArea.text = areaItem
            for ((index, item) in areaArray.withIndex()) {

                if (areaItem == item) {
                    indexArrayForAllLovs[1] = index
                    if (areaItem.isNotEmpty() && areaItem != "") {
                        if (branchArray.isNotEmpty()) {
                            branchArray.clear()
                        }
                        val branch =
                            lovsModel.get(indexArrayForAllLovs[0]).area.get(indexArrayForAllLovs[0]).branch

                        for (item in branch) {
                            branchArray.add(item.branch_name)
                        }

                        if (branchArray.isNotEmpty()) {
                            showBranchArray(branchArray, getSetFilterModel.selected_branch)
                        }
                    }

                }
            }
        }
    }

    private fun showBranchArray(branchArray: ArrayList<String>, currentBranch: String = "") {
        binding.actvBranch.text = ""

        binding.actvBranch.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            val branchIconSpinnerItems = branchArray.map { IconSpinnerItem(text = it) }
            setItems(branchIconSpinnerItems)
            getSpinnerRecyclerView().layoutManager = LinearLayoutManager(context)
            getSpinnerRecyclerView().layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

        }


        binding.actvBranch.setOnSpinnerItemSelectedListener<IconSpinnerItem> { oldIndex, oldItem, newIndex, newItem ->


            branchItem = newItem.text.toString()
            branchCode = branchDict[newItem.text.toString()].toString()
            Log.d( "showBranchArray: ",branchItem.toString())

//            binding.actvBranch.clearFocus()
        }

        if (currentBranch != "") {
            branchItem = currentBranch
            branchCode = branchDict[currentBranch].toString()
            Log.d("TAG", branchItem.toString())
            binding.actvBranch.text = branchItem


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnClickListener() {
        binding.let {
            it.ivRegionCross.setOnClickListener {
                areaArray = arrayListOf()
                branchArray = arrayListOf()
                binding.actvRegion.text = ""
                binding.actvArea.text = ""
                binding.actvBranch.text = ""
                regionItem = ""
                areaItem = ""
                branchItem = ""

                for (item in lovsModel) {
                    item.area.forEach {
                        areaArray.add(it.area_name)
                        it.branch.forEach {
                            branchArray.add(it.branch_name)
                        }
                    }
                }
                binding.actvArea.apply {
                    setSpinnerAdapter(IconSpinnerAdapter(this))
                    val areaIconSpinnerItems = areaArray.map { IconSpinnerItem(text = it) }
                    setItems(areaIconSpinnerItems)
                    getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)

                }

                binding.actvBranch.apply {
                    setSpinnerAdapter(IconSpinnerAdapter(this))
                    val branchIconSpinnerItems = branchArray.map { IconSpinnerItem(text = it) }
                    setItems(branchIconSpinnerItems)
                    getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)


                }

                indexArrayForAllLovs.fill(0)

            }

            it.ivAreaCross.setOnClickListener {
                branchArray = arrayListOf()
                binding.actvArea.text = ""
                binding.actvBranch.text = ""
                areaItem = ""
                branchItem = ""
                for (item in lovsModel) {
                    item.area.forEach {
                        it.branch.forEach {
                            branchArray.add(it.branch_name)
                        }
                    }
                }
                binding.actvBranch.apply {
                    setSpinnerAdapter(IconSpinnerAdapter(this))
                    val branchIconSpinnerItems = branchArray.map { IconSpinnerItem(text = it) }
                    setItems(branchIconSpinnerItems)
                    getSpinnerRecyclerView().layoutManager = LinearLayoutManager(context)
                    getSpinnerRecyclerView().layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

                }
                indexArrayForAllLovs[1] = 0
                indexArrayForAllLovs[2] = 0
            }

            it.ivBranchCross.setOnClickListener {
                binding.actvBranch.text = ""
                branchItem = ""
                indexArrayForAllLovs[2] = 0
            }

            it.ivDateCross.setOnClickListener {
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
                val formattedDate = currentDate.format(formatter)
                selectedDate = formattedDate
                binding.actvDate.setText(selectedDate)
            }

            it.actvDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, selectedYear, selectedMonth, selectedDay ->
                        selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                        binding.actvDate.setText(selectedDate)
                    },
                    year,
                    month,
                    dayOfMonth
                )
                datePickerDialog.show()
            }

            it.acbBtn.setOnClickListener {

                val setFilterResponse: SetFilterRequestDataModel = SetFilterRequestDataModel(
                    selected_region = regionItem,
                    selected_area = areaItem,
                    selected_branch = branchCode,
                    selected_date = selectedDate
                )
                setFilter(
                    setFilterResponse.selected_area,
                    setFilterResponse.selected_region,
                    setFilterResponse.selected_branch,
                    setFilterResponse.selected_date
                )
            }

            it.acbReset.setOnClickListener {
                resetFilter()
            }
            it.actvRegion.setOnSpinnerOutsideTouchListener { view, event ->
                it.actvRegion.dismiss()
            }

            it.actvArea.setOnSpinnerOutsideTouchListener { view, event ->
                it.actvArea.dismiss()
            }

            it.actvBranch.setOnSpinnerOutsideTouchListener { view, event ->
                it.actvBranch.dismiss()
            }


        }

    }

    private fun getLovs() {
        myDockActivity?.showProgressIndicator()
        myViewModel.viewModelScope.launch { myViewModel.getLovs() }
    }

    private fun getSetFilter() {
        myDockActivity?.showProgressIndicator()
        myViewModel.viewModelScope.launch { myViewModel.getSetFilter() }
    }

    private fun resetFilter() {
        myDockActivity?.showProgressIndicator()
        myViewModel.viewModelScope.launch { myViewModel.resetFilter() }
    }

    private fun setFilter(
        selected_area: String,
        selected_region: String,
        selected_branch: String,
        selected_date: String,
    ) {
        myViewModel.viewModelScope.launch {
            myDockActivity?.showProgressIndicator()
            myViewModel.setFilter(
                selected_area, selected_region, selected_branch, selected_date
            )
        }
    }

    private fun handleAPIResponse() {

        lifecycleScope.launch {
            myViewModel.getLovResponse.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)

                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {
                        myDockActivity?.hideProgressIndicator()
                        var indexes = 0
                        regionArray = arrayListOf()
                        areaArray = arrayListOf()
                        branchArray = arrayListOf()
                        branchDict = hashMapOf()
                        lovsModel = it.data?.body()!!
                        if (lovsModel != null && lovsModel.isNotEmpty()) {

                            for (item in lovsModel) {
                                regionArray.add(item.region_name)
                                item.area.forEach {
                                    areaArray.add(it.area_name)
                                    it.branch.forEach {
                                        branchDict[it.branch_name] = it.branch_code
                                        branchArray.add(it.branch_name)

                                    }
                                }
                            }

                            if (regionArray.isNotEmpty()){
                                showRegionArray(
                                    regionArray,
                                    getSetFilterModel.selected_region
                                )
                            }
                            if (areaArray.isNotEmpty()) {
                                showAreaArray(
                                    areaArray,
                                    getSetFilterModel.selected_area
                                )
                            }
                            if (branchArray.isNotEmpty()){
                                showBranchArray(
                                    branchArray,
                                    getSetFilterModel.selected_branch_name
                                )
                            }

                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            myViewModel.getSetFilterResponse.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                        Toast.makeText(
                            requireContext(), "error: " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {

                        getSetFilterModel = it.data?.body()!!

                        if (getSetFilterModel != null) {
                            selectedDate = getSetFilterModel.selected_date
                            binding.actvDate.setText(selectedDate)
                            getLovs()
                        }
                    }


                }
            }

        }

        lifecycleScope.launch {
            myViewModel.setFilterResponse.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                        Toast.makeText(
                            requireContext(), "error: " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {

                        val setFilterResponse = it.data?.body()!!

                        if (setFilterResponse != null) {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                    }


                }
            }

        }

        lifecycleScope.launch {
            myViewModel.resetFilterResponse.collect {
                myDockActivity?.hideProgressIndicator()
                when (it) {
                    is ResponseModel.Error -> {
                        (requireActivity() as AppCompatActivity).handleErrorResponse(it)
                        Toast.makeText(
                            requireContext(), "error: " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseModel.Idle -> {
                    }

                    is ResponseModel.Loading -> {
                    }

                    is ResponseModel.Success -> {

                        val resetFilterResponse = it.data?.body()!!

                        if (resetFilterResponse != null && resetFilterResponse.success.isNotEmpty()) {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }

        }
    }

}