package com.fintrack.app.ui.statistik

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fintrack.app.R
import com.fintrack.app.data.DummyTransactions
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.google.android.material.tabs.TabLayout
import java.text.NumberFormat
import java.util.*

class StatistikTabFragment : Fragment() {

    companion object {
        private const val ARG_TYPE = "type"
        private const val ARG_DATE = "date"
        private const val ARG_MODE = "mode"

        fun newInstance(type: String, date: Date, mode: String): StatistikTabFragment {
            val fragment = StatistikTabFragment()
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            args.putLong(ARG_DATE, date.time)
            args.putString(ARG_MODE, mode)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var pieChart: PieChart
    private lateinit var tabChart: TabLayout
    private lateinit var layoutDetails: LinearLayout
    private lateinit var txtKosong: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab_statistik, container, false)
        pieChart = view.findViewById(R.id.piechart)
        tabChart = view.findViewById(R.id.tabchart)
        layoutDetails = view.findViewById(R.id.layoutDetails)
        txtKosong = view.findViewById(R.id.txt_noData)

        val type = arguments?.getString(ARG_TYPE)
        val date = arguments?.getLong(ARG_DATE)?.let { Date(it) }
        val mode = arguments?.getString(ARG_MODE)

        setupPieChart(type, date, mode)
        return view
    }

    private fun setupPieChart(type: String?, date: Date?, mode: String?) {
        val transactions = DummyTransactions.list
        val context = requireContext()

        val filtered = transactions.filter {
            if (mode == "Bulanan") {
                it.createdAt.month == date?.month && it.createdAt.year == date.year
            } else {
                it.createdAt.year == date?.year
            }
        }

        val entries = mutableListOf<PieEntry>()
        val labelToColor = mutableMapOf<String, Int>()
        layoutDetails.removeAllViews()

        val grouped = when (type) {
            "pendapatan" -> filtered.filter { it.type == "income" }.groupBy { it.categoryId.name }
            "pengeluaran" -> filtered.filter { it.type == "expense" }.groupBy { it.categoryId.name }
            else -> null
        }

        if (!grouped.isNullOrEmpty()) {
            txtKosong.visibility = View.GONE
            pieChart.visibility = View.VISIBLE
            layoutDetails.visibility = View.VISIBLE

            var colorIndex = 0
            val baseColor = if (type == "pendapatan")
                ContextCompat.getColor(context, R.color.primaryBlue)
            else
                ContextCompat.getColor(context, R.color.primaryRed)

            val generatedColors = generateShades(baseColor, grouped.size)

            val total = grouped.values.flatten().sumOf { it.amount }

            grouped.forEach { (label, items) ->
                val sum = items.sumOf { it.amount }
                entries.add(PieEntry(sum.toFloat(), label))
                val color = generatedColors[colorIndex++ % generatedColors.size]
                labelToColor[label] = color

                val row = layoutDetailsRow(label, sum, total, color)
                layoutDetails.addView(row)
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = entries.map { labelToColor[it.label] ?: Color.GRAY }
            dataSet.setDrawValues(false)
            pieChart.data = PieData(dataSet)

        } else if (grouped != null && grouped.isEmpty()) {
            // Tidak ada data income/expense
            txtKosong.visibility = View.VISIBLE
            pieChart.visibility = View.GONE
            tabChart.visibility = View.GONE
            layoutDetails.visibility = View.GONE
            return
        } else {
            // type == keseluruhan
            val incomeTotal = filtered.filter { it.type == "income" }.sumOf { it.amount }
            val expenseTotal = filtered.filter { it.type == "expense" }.sumOf { it.amount }

            if (incomeTotal == 0 && expenseTotal == 0) {
                txtKosong.visibility = View.VISIBLE
                pieChart.visibility = View.GONE
                tabChart.visibility = View.GONE
                layoutDetails.visibility = View.GONE
                return
            } else {
                txtKosong.visibility = View.GONE
                pieChart.visibility = View.VISIBLE
                layoutDetails.visibility = View.VISIBLE
            }

            entries.add(PieEntry(incomeTotal.toFloat(), "Pendapatan"))
            entries.add(PieEntry(expenseTotal.toFloat(), "Pengeluaran"))

            val colorIncome = ContextCompat.getColor(context, R.color.primaryBlue)
            val colorExpense = ContextCompat.getColor(context, R.color.primaryRed)
            labelToColor["Pendapatan"] = colorIncome
            labelToColor["Pengeluaran"] = colorExpense

            entries.forEach {
                val row = layoutDetailsRow(it.label, it.value.toInt(), incomeTotal + expenseTotal, labelToColor[it.label]!!)
                layoutDetails.addView(row)
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = listOf(colorIncome, colorExpense)
            dataSet.setDrawValues(false)
            pieChart.data = PieData(dataSet)
        }

        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.invalidate()
    }

//    details pada tiap2 chart
    private fun layoutDetailsRow(label: String, value: Int, total: Int, color: Int): View {
        val row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row.setPadding(16, 8, 16, 8)

        val percent = (value.toFloat() / total.toFloat()) * 100
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        val leftLayout = LinearLayout(requireContext())
        leftLayout.orientation = LinearLayout.HORIZONTAL
        leftLayout.layoutParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )

        val tvPercent = TextView(requireContext())
        tvPercent.text = String.format("%.0f%%", percent)
        tvPercent.setBackgroundColor(color)
        tvPercent.setTextColor(Color.WHITE)
        tvPercent.setPadding(0, 8, 0, 8)
        tvPercent.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tvPercent.gravity = Gravity.CENTER

        val percentParams = LinearLayout.LayoutParams(
            (64 * resources.displayMetrics.density).toInt(), // 64dp in px
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        percentParams.setMargins(0, 0, 16, 0)
        tvPercent.layoutParams = percentParams


        val tvLabel = TextView(requireContext())
        tvLabel.text = label
        tvLabel.setTextColor(Color.BLACK)
        tvLabel.setPadding(0, 0, 16, 0)

        leftLayout.addView(tvPercent)
        leftLayout.addView(tvLabel)

        val tvAmount = TextView(requireContext())
        tvAmount.text = format.format(value)
        tvAmount.setTextColor(color)
        tvAmount.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        tvAmount.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        row.addView(leftLayout)
        row.addView(tvAmount)

        return row
    }

    //    autogenerate warna chart pada pengeluaran dan pendapatan
    private fun generateShades(baseColor: Int, count: Int): List<Int> {
        val result = mutableListOf<Int>()
        val factor = 0.85f
        var current = baseColor

        for (i in 0 until count) {
            result.add(current)
            current = darkenColor(current, factor)
        }
        return result
    }

    private fun darkenColor(color: Int, factor: Float): Int {
        val r = (Color.red(color) * factor).toInt().coerceAtLeast(0)
        val g = (Color.green(color) * factor).toInt().coerceAtLeast(0)
        val b = (Color.blue(color) * factor).toInt().coerceAtLeast(0)
        return Color.rgb(r, g, b)
    }
}
