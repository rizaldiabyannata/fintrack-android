package com.fintrack.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fintrack.app.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Date

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_tab_statistik, container, false)
        pieChart = view.findViewById(R.id.piechart)

        val type = arguments?.getString(ARG_TYPE)
        val date = arguments?.getLong(ARG_DATE)?.let { Date(it) }
        val mode = arguments?.getString(ARG_MODE)

        setupPieChart(type, date, mode)
        return view
    }

    private fun setupPieChart(type: String?, date: Date?, mode: String?) {
        // Simulasi data dummy berdasarkan type + mode
        val entries = when (type) {
            "pendapatan" -> {
                if (mode == "Bulanan") {
                    listOf(
                        PieEntry(60f, "Gaji"),
                        PieEntry(25f, "Bonus"),
                        PieEntry(15f, "UMKM")
                    )
                } else {
                    listOf(
                        PieEntry(70f, "Gaji Tahunan"),
                        PieEntry(20f, "Bonus Tahunan"),
                        PieEntry(10f, "UMKM Tahunan")
                    )
                }
            }

            "pengeluaran" -> {
                if (mode == "Bulanan") {
                    listOf(
                        PieEntry(40f, "Makan"),
                        PieEntry(30f, "Transportasi"),
                        PieEntry(30f, "Hiburan")
                    )
                } else {
                    listOf(
                        PieEntry(45f, "Makan Tahunan"),
                        PieEntry(30f, "Transportasi Tahunan"),
                        PieEntry(25f, "Lain-lain")
                    )
                }
            }

            else -> {
                if (mode == "Bulanan") {
                    listOf(
                        PieEntry(65f, "Pendapatan"),
                        PieEntry(35f, "Pengeluaran")
                    )
                } else {
                    listOf(
                        PieEntry(75f, "Pendapatan Tahunan"),
                        PieEntry(25f, "Pengeluaran Tahunan")
                    )
                }
            }
        }

        val colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.primaryBlue),
            ContextCompat.getColor(requireContext(), R.color.primaryRed),
            ContextCompat.getColor(requireContext(), R.color.black)
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.selectionShift = 5f
        dataSet.setDrawValues(false)

        pieChart.data = PieData(dataSet)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.invalidate()
    }

}