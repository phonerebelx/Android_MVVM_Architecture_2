import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class PieChartBuilder(private val context: Context, private val pieChart: PieChart) {
    private var description: String = ""
    private var xOffset: Float = 0f
    private var yOffset: Float = 0f
    private var centerText: String = ""
    private var holeColor: Int = Color.parseColor("#E0E0E0")
    private var centerTextSize: Float = 20f
    private var centerTextColor: Int = Color.parseColor("#7B7878")
    private var pieEntries: MutableList<PieEntry> = mutableListOf()
    private var colors: ArrayList<Int> = ArrayList()
    private var legendEntries: Array<LegendEntry>? = null
    private var legendEnabled: Boolean = false
    private var legendAlignment: Legend.LegendHorizontalAlignment? = null
    private var extraRightOffset: Float = 0f
    private var extraLeftOffset: Float = 0f

    fun extraRightOffset(offset: Float): PieChartBuilder {
        this.extraRightOffset = offset
        return this
    }

    fun extraLeftOffset(offset: Float): PieChartBuilder {
        this.extraLeftOffset = offset
        return this
    }

    fun description(description: String): PieChartBuilder {
        this.description = description
        return this
    }

    fun xOffset(xOffset: Float): PieChartBuilder {
        this.xOffset = xOffset
        return this
    }

    fun yOffset(yOffset: Float): PieChartBuilder {
        this.yOffset = yOffset
        return this
    }

    fun centerText(centerText: String): PieChartBuilder {
        this.centerText = centerText
        return this
    }

    fun holeColor(holeColor: Int): PieChartBuilder {
        this.holeColor = holeColor
        return this
    }

    fun centerTextSize(centerTextSize: Float): PieChartBuilder {
        this.centerTextSize = centerTextSize
        return this
    }

    fun centerTextColor(centerTextColor: Int): PieChartBuilder {
        this.centerTextColor = centerTextColor
        return this
    }

    fun pieEntries(pieEntries: MutableList<PieEntry>): PieChartBuilder {
        this.pieEntries = pieEntries
        return this
    }

    fun colors(colors: ArrayList<Int>): PieChartBuilder {
        this.colors = colors
        return this
    }

    fun legendEntries(legendEntries: Array<LegendEntry>?): PieChartBuilder {
        this.legendEntries = legendEntries
        return this
    }

    fun legendEnabled(enabled: Boolean): PieChartBuilder {
        this.legendEnabled = enabled
        return this
    }

    fun legendAlignment(alignment: Legend.LegendHorizontalAlignment?): PieChartBuilder {
        this.legendAlignment = alignment
        return this
    }

    fun build() {
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = colors
        pieDataSet.valueTextSize = 0f
        pieDataSet.selectionShift = 0f

        pieChart.apply {
            description.text = this@PieChartBuilder.description
            description.xOffset = this@PieChartBuilder.xOffset
            description.yOffset = this@PieChartBuilder.yOffset
            centerText = this@PieChartBuilder.centerText
            setHoleColor(this@PieChartBuilder.holeColor)
            setCenterTextSize(this@PieChartBuilder.centerTextSize)
            setCenterTextColor(this@PieChartBuilder.centerTextColor)
            setExtraOffsets(
                this@PieChartBuilder.extraLeftOffset,
                0f,
                this@PieChartBuilder.extraRightOffset,
                0f
            )
            data = PieData(pieDataSet)
            invalidate()
        }

        if (legendEnabled) {
            val legend: Legend = pieChart.legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = legendAlignment ?: Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.yEntrySpace = 5f
            legend.xOffset = 10f

            legendEntries?.let { legend.setCustom(it) }
            legend.isEnabled = true
        }
    }
}
