package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.TransactionRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.response.DailySummaryResponse
import com.fintrack.app.databinding.FragmentTransaksiBinding
import com.fintrack.app.ui.TransaksiTabAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TransaksiFragment : Fragment() {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        setupMonthNavigation()
        setupClickListeners()
        setupSwipeToRefresh()

        fetchAndProcessTransactions()
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchAndProcessTransactions()
        }
    }

    private fun setupTabs() {
        binding.viewPager.adapter = TransaksiTabAdapter(this)
        val tabTitles = listOf("Harian", "Bulanan", "Tutup Buku")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupClickListeners() {
        binding.fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddTransactionFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupMonthNavigation() {
        updateMonthYearText()
        binding.btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthYearText()
            fetchAndProcessTransactions()
        }
        binding.btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthYearText()
            fetchAndProcessTransactions()
        }
    }

    private fun fetchAndProcessTransactions() {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = true
        }

        lifecycleScope.launch {
            transactionRepository.getAllTransactions(month, year).collect { response ->
                binding.swipeRefreshLayout.isRefreshing = false
                when (response) {
                    is ApiResponse.Loading -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    is ApiResponse.Success -> {
                        handleSuccessfulResponse(response.data)
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, response.errorMessage, Toast.LENGTH_LONG).show()
                        updateHarianTab(emptyList())
                        updateBulananTab(emptyList())
                        updateInfoCards(0.0, 0.0)
                    }
                }
            }
        }
    }

    private fun handleSuccessfulResponse(dailySummaries: List<DailySummaryResponse>) {
        if (dailySummaries.isNullOrEmpty()) {
            Toast.makeText(context, "Tidak ada data untuk bulan ini.", Toast.LENGTH_SHORT).show()
            updateHarianTab(emptyList())
            updateBulananTab(emptyList())
            updateInfoCards(0.0, 0.0)
        } else {
            val harianData = processSummariesToHarian(dailySummaries)
            updateHarianTab(harianData)

            val bulananData = processSummariesToBulananCard(dailySummaries)
            updateBulananTab(listOf(bulananData))

            val totalIncome = dailySummaries.map { it.income.toString().toDoubleOrNull() ?: 0.0 }.sum()
            val totalExpense = dailySummaries.map { it.expense.toString().toDoubleOrNull() ?: 0.0 }.sum()
            updateInfoCards(totalIncome, totalExpense)
        }
    }

    private fun updateInfoCards(income: Double, expense: Double) {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0

        binding.tvIncomeValue.text = format.format(income)
        binding.tvExpenseValue.text = format.format(expense)
        binding.tvTotalValue.text = format.format(income - expense)
    }

    private fun updateHarianTab(data: List<RangkumanHarian>) {
        binding.viewPager.post {
            val harianFragment = childFragmentManager.fragments.find { it is TabHarianFragment } as? TabHarianFragment
            harianFragment?.submitHarianData(data)
        }
    }

    private fun updateBulananTab(data: List<RangkumanBulanan>) {
        binding.viewPager.post {
            val bulananFragment = childFragmentManager.fragments.find { it is TabBulananFragment } as? TabBulananFragment
            bulananFragment?.submitBulananData(data)
        }
    }

    private fun updateMonthYearText() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        binding.txtMonthYear.text = sdf.format(calendar.time)
    }

    private fun parseDate(dateString: String?): Date? {
        if (dateString == null) return null
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        for (format in formats) {
            try {
                return format.parse(dateString)
            } catch (e: Exception) {
            }
        }
        return null
    }

    private fun processSummariesToHarian(summaries: List<DailySummaryResponse>): List<RangkumanHarian> {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }

        return summaries.mapNotNull { summary ->
            val summaryDate = parseDate(summary.date) ?: return@mapNotNull null
            val dayFormat = SimpleDateFormat("E", Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val detailHarian = summary.transactions?.mapNotNull { trx ->
                val detailDate = parseDate(trx.createdAt) ?: return@mapNotNull null
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                DataHarian(
                    waktu = timeFormat.format(detailDate),
                    jenis = trx.description ?: "Tanpa Deskripsi",
                    media = (trx.category as? String) ?: "Lainnya",
                    pendapatan = if (trx.type == "income") formatRupiah.format(trx.amount) else "Rp. 0",
                    pengeluaran = if (trx.type == "expense") formatRupiah.format(trx.amount) else "Rp. 0"
                )
            } ?: emptyList()

            RangkumanHarian(
                hari = dayFormat.format(summaryDate),
                tanggal = dateFormat.format(summaryDate),
                totalPendapatan = formatRupiah.format(summary.income.toString().toDoubleOrNull() ?: 0.0),
                totalPengeluaran = formatRupiah.format(summary.expense.toString().toDoubleOrNull() ?: 0.0),
                detail = detailHarian
            )
        }.sortedByDescending { it.tanggal }
    }

    private fun processSummariesToBulananCard(summaries: List<DailySummaryResponse>): RangkumanBulanan {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
        val sdfBulan = SimpleDateFormat("MMMM", Locale("id", "ID"))
        val sdfTahun = SimpleDateFormat("yyyy", Locale.getDefault())
        val sdfTanggal = SimpleDateFormat("dd", Locale.getDefault())

        val firstDate = parseDate(summaries.firstOrNull()?.date)
        val totalIncome = summaries.map { it.income.toString().toDoubleOrNull() ?: 0.0 }.sum()
        val totalExpense = summaries.map { it.expense.toString().toDoubleOrNull() ?: 0.0 }.sum()

        val detailHarianList = summaries.mapNotNull { summary ->
            val summaryDate = parseDate(summary.date) ?: return@mapNotNull null
            DataBulanan(
                tanggal = sdfTanggal.format(summaryDate),
                pendapatan = formatRupiah.format(summary.income.toString().toDoubleOrNull() ?: 0.0),
                pengeluaran = formatRupiah.format(summary.expense.toString().toDoubleOrNull() ?: 0.0)
            )
        }.sortedBy { it.tanggal }

        return RangkumanBulanan(
            bulan = if (firstDate != null) sdfBulan.format(firstDate) else "N/A",
            tahun = if (firstDate != null) sdfTahun.format(firstDate) else "",
            totalPendapatan = formatRupiah.format(totalIncome),
            totalPengeluaran = formatRupiah.format(totalExpense),
            detail = detailHarianList
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}