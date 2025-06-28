package com.fintrack.app.ui.statistik

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fintrack.app.R
import com.fintrack.app.data.StatisticRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.response.GetStatAllResponse
import com.fintrack.app.data.response.GetStatWithTypeResponse
import com.fintrack.app.databinding.FragmentTabStatistikBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StatistikTabFragment : Fragment() {

    companion object {
        private const val ARG_TYPE = "type"
        private const val ARG_DATE = "date"
        private const val ARG_MODE = "mode"

        fun newInstance(type: String, date: Date, mode: String): StatistikTabFragment {
            return StatistikTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, type)
                    putLong(ARG_DATE, date.time)
                    putString(ARG_MODE, mode)
                }
            }
        }
    }

    // Menggunakan Hilt untuk menginjeksi Repository secara langsung ke Fragment
    @Inject
    lateinit var statisticRepository: StatisticRepository

    private var _binding: FragmentTabStatistikBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabStatistikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewDetails.layoutManager = LinearLayoutManager(requireContext())

        val type = arguments?.getString(ARG_TYPE)
        val date = arguments?.getLong(ARG_DATE)?.let { Date(it) }
        val mode = arguments?.getString(ARG_MODE)

        if (date != null && mode != null && type != null) {
            fetchStatistics(mode, date, type)
        }
    }

    private fun fetchStatistics(mode: String, date: Date, type: String) {
        // Menggunakan lifecycleScope agar coroutine otomatis berhenti saat Fragment hancur
        viewLifecycleOwner.lifecycleScope.launch {
            // Konversi tipe dari UI ("pendapatan") ke tipe API ("income")
            val apiType = when (type) {
                "pendapatan" -> "income"
                "pengeluaran" -> "expense"
                else -> null // Untuk "keseluruhan"
            }

            val calendar = Calendar.getInstance().apply { time = date }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1

            val dataFlow = if (mode == "Bulanan") {
                statisticRepository.getMonthlyStats(year, month, apiType)
            } else {
                statisticRepository.getYearlyStats(year, apiType)
            }

            // Mengamati Flow dari Repository
            dataFlow.collectLatest { response ->
                when (response) {
                    is ApiResponse.Loading -> showLoading()
                    is ApiResponse.Success -> handleSuccessResponse(response.data)
                    is ApiResponse.Error -> showError(response.errorMessage)
                }
            }
        }
    }

    private fun handleSuccessResponse(data: Any?) {
        when (data) {
            is GetStatAllResponse -> setupOverallPieChart(data)
            is GetStatWithTypeResponse -> setupTypedPieChart(data)
            else -> showError("Tipe data respons tidak valid.")
        }
    }

    // Fungsi untuk menampilkan data "keseluruhan" (income vs expense)
    private fun setupOverallPieChart(data: GetStatAllResponse) {
        val incomeTotal = data.summary?.totalIncome ?: 0
        val expenseTotal = data.summary?.totalExpense ?: 0

        if (incomeTotal == 0 && expenseTotal == 0) {
            showEmptyData()
            return
        }

        showDataView()

        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        val detailItems = mutableListOf<DetailItem>()
        val total = incomeTotal + expenseTotal

        if (incomeTotal > 0) {
            entries.add(PieEntry(incomeTotal.toFloat(), "Pendapatan"))
            val color = ContextCompat.getColor(requireContext(), R.color.primaryBlue)
            colors.add(color)
            detailItems.add(DetailItem("Pendapatan", incomeTotal, total, color))
        }

        if (expenseTotal > 0) {
            entries.add(PieEntry(expenseTotal.toFloat(), "Pengeluaran"))
            val color = ContextCompat.getColor(requireContext(), R.color.primaryRed)
            colors.add(color)
            detailItems.add(DetailItem("Pengeluaran", expenseTotal, total, color))
        }

        detailItems.sortByDescending { it.value }

        updatePieChart(entries, colors)
        binding.recyclerViewDetails.adapter = ItemDetailAdapter(detailItems)
    }

    /**
     * --- FUNGSI YANG DIPERBAIKI ---
     * Fungsi ini sekarang akan menampilkan rincian berdasarkan nama kategori,
     * bukan lagi berdasarkan tipe grup kategori.
     */
    private fun setupTypedPieChart(response: GetStatWithTypeResponse) {
        val type = arguments?.getString(ARG_TYPE) ?: "pengeluaran"

        // Menggabungkan semua rincian kategori dari setiap grup menjadi satu list
        val allCategoryDetails = response.data?.breakdownByType?.flatMap { group ->
            group?.details?.filterNotNull() ?: emptyList()
        }

        if (allCategoryDetails.isNullOrEmpty()) {
            showEmptyData()
            return
        }

        showDataView()

        val entries = mutableListOf<PieEntry>()
        val detailItemsForAdapter = mutableListOf<DetailItem>()

        // Total keseluruhan adalah jumlah dari semua kategori.
        val grandTotal = allCategoryDetails.sumOf { it.totalAmount ?: 0 }

        val baseColor = if (type == "pendapatan")
            ContextCompat.getColor(requireContext(), R.color.primaryBlue)
        else
            ContextCompat.getColor(requireContext(), R.color.primaryRed)

        val colors = generateShades(baseColor, allCategoryDetails.size)

        // Iterasi melalui setiap kategori untuk membuat pie slice dan item list
        allCategoryDetails.sortedByDescending { it.totalAmount }.forEachIndexed { index, category ->
            if (category.totalAmount != null && category.totalAmount > 0) {
                val label = category.categoryName ?: "Lainnya"
                entries.add(PieEntry(category.totalAmount.toFloat(), label))

                // Buat DetailItem untuk RecyclerView
                detailItemsForAdapter.add(
                    DetailItem(
                        label = label,
                        value = category.totalAmount,
                        total = grandTotal, // Gunakan total keseluruhan untuk persentase
                        color = colors[index % colors.size]
                    )
                )
            }
        }

        updatePieChart(entries, colors)
        binding.recyclerViewDetails.adapter = ItemDetailAdapter(detailItemsForAdapter)
    }

    private fun updatePieChart(entries: List<PieEntry>, colors: List<Int>) {
        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            setDrawValues(false)
        }
        binding.piechart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            isDrawHoleEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            invalidate()
        }
    }

    // --- Fungsi untuk Mengatur Visibilitas UI ---

    private fun showLoading() {
        // Karena layout tidak memiliki ProgressBar, kita sembunyikan semua view
        // dan tampilkan pesan loading pada txtNoData
        binding.piechart.visibility = View.GONE
        binding.recyclerViewDetails.visibility = View.GONE
        binding.txtNoData.visibility = View.VISIBLE
        binding.txtNoData.text = "Memuat data..."
    }

    private fun showDataView() {
        binding.piechart.visibility = View.VISIBLE
        binding.recyclerViewDetails.visibility = View.VISIBLE
        binding.txtNoData.visibility = View.GONE
    }

    private fun showEmptyData() {
        binding.piechart.visibility = View.GONE
        binding.recyclerViewDetails.visibility = View.GONE
        binding.txtNoData.visibility = View.VISIBLE
        binding.txtNoData.text = "Tidak ada data untuk periode ini."
    }

    private fun showError(message: String) {
        binding.piechart.visibility = View.GONE
        binding.recyclerViewDetails.visibility = View.GONE
        binding.txtNoData.visibility = View.VISIBLE
        binding.txtNoData.text = message
    }

    private fun generateShades(baseColor: Int, count: Int): List<Int> {
        if (count <= 0) return emptyList()
        val result = mutableListOf<Int>()
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        for (i in 0 until count) {
            hsv[2] = 1.0f - (i.toFloat() / (count * 1.5f))
            result.add(Color.HSVToColor(hsv))
        }
        return result.reversed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
