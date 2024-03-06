package com.example.meezan360.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meezan360.R
import com.example.meezan360.adapter.levelTwoAdapter.StepProgressBarAdapter
import com.example.meezan360.databinding.ReportItemParentBinding
import com.example.meezan360.databinding.StepProgressBarItemBinding
import com.example.meezan360.interfaces.OnTypeItemClickListener
import com.example.meezan360.model.reports.Column
import com.example.meezan360.model.reports.Report
import com.example.meezan360.model.reports.ReportDataArrayModel
import com.example.meezan360.model.reports.ReportsColumnData
import com.example.meezan360.ui.activities.CardLevel.CardLevelActivity
import com.example.meezan360.ui.activities.ReportLevel2Activity
import kotlin.math.log


class ReportParentAdapter(
    private var myContext: Context,
    private val reportList: ArrayList<Report>?,
    private var kpiId: String = ""
) : RecyclerView.Adapter<ReportParentAdapter.ViewHolder>(), OnTypeItemClickListener {
    private var selectedPosition = 0
    lateinit var binding: ReportItemParentBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ReportItemParentBinding.inflate(LayoutInflater.from(myContext), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = reportList?.get(position)

        binding.tvRepItemTitle?.text = item?.tableTitle

//        identifierType = item.column?.get(position).header ?:
        if (item == null) {
            return
        }

        //it creates textview for header dynamically on the basis of column items

        item.column?.forEachIndexed { index, column ->

            val valueTV = TextView(myContext)
            val typeface = ResourcesCompat.getFont(myContext, R.font.montserrat_light)


            valueTV.text = column.header
            valueTV.typeface = typeface
            valueTV.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                myContext.resources.getDimension(com.intuit.sdp.R.dimen._12sdp)
            )
            valueTV.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            valueTV.setTextColor(ContextCompat.getColor(myContext,R.color.white))
            valueTV.layoutParams = LinearLayout.LayoutParams(
                myContext.resources.getDimension(com.intuit.sdp.R.dimen._75sdp).toInt(),
                LinearLayout.LayoutParams.MATCH_PARENT

            ).apply {
                gravity = Gravity.CENTER
            }
            binding.headerLayout?.addView(valueTV)
        }


        val columnsData = item.column?.let { Report.getDataArray(it) }


        val verticalAdapter = item.column?.get(0)?.data?.let {
            if (columnsData != null) {
                ReportChildVerticalAdapter(
                    myContext,
                    it,
                    columnsData,
                    item.tableId, this
                )
            } else {
                ReportChildVerticalAdapter(myContext, it, arrayListOf(), item.tableId, this)
            }
        }

        binding.rvVerticalChild.adapter = verticalAdapter

    }

    override fun getItemCount(): Int {
        return reportList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            this.setIsRecyclable(false)
        }

        var tvRepItemTitle: TextView? = itemView.findViewById(R.id.tvRepItemTitle)
        val rvVerticalChild: RecyclerView? = itemView.findViewById(R.id.rvVerticalChild)
        val headerLayout: LinearLayout? = itemView.findViewById(R.id.headerLayout)
    }

    override fun <T> onClick(type: String, item: T, position: Int, checked: Boolean?) {
        val item = item as ArrayList<ReportsColumnData>


        reportList?.forEachIndexed { index, report ->
            report.column?.forEachIndexed { columnIndex, column ->
                column.data?.forEachIndexed { dataIndex, data ->
                    if (item.get(0).value == data.value) {
                        val identifier_type = column.identifier_type ?: ""
                        val identifier = data.identifier ?: ""

                        if (report.isClickable == "yes") {
                            when (report.innerReportType) {

                                "card" -> {
                                    val intent = Intent(myContext, CardLevelActivity::class.java)
                                    intent.putExtra("kpiId", kpiId)
                                    intent.putExtra("tableId", report.tableId)
                                    myContext.startActivity(intent)
                                }

                                "table" -> {
                                    val intent = Intent(myContext, ReportLevel2Activity::class.java)
                                    intent.putExtra("kpiId", kpiId)
                                    intent.putExtra("tableId", report.tableId)
                                    intent.putExtra(
                                        "identifierType",
                                        identifier_type
                                    )
                                    intent.putExtra("identifier", identifier)
                                    myContext.startActivity(intent)
                                }
                            }
                        }
                    }
                }

            }
        }

    }
}