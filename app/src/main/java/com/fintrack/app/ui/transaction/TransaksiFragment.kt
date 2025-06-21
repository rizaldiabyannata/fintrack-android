package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.fintrack.app.R
import com.fintrack.app.data.TransactionRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.response.DailySummaryResponse
import com.fintrack.app.ui.TransaksiTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@AndroidEntryPoint
class TransaksiFragment : Fragment(R.layout.fragment_transaksi) {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var txtMonthYear: TextView
    private lateinit var btnPrevMonth: ImageView
    private lateinit var btnNextMonth: ImageView

    private val calendar: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupTabs()
        setupMonthNavigation()
        fetchAndProcessTransactions()
    }

    private fun initializeViews(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        txtMonthYear = view.findViewById(R.id.txt_month_year)
        btnPrevMonth = view.findViewById(R.id.btn_prev_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)
    }

    private fun setupTabs() {
        viewPager.adapter = TransaksiTabAdapter(this)
        val tabTitles = listOf("Harian", "Bulanan", "Tutup Buku")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupMonthNavigation() {
        updateMonthYearText()
        btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthYearText()
            fetchAndProcessTransactions()
        }
        btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthYearText()
            fetchAndProcessTransactions()
        }
    }

    private fun fetchAndProcessTransactions() {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        lifecycleScope.launch {
            transactionRepository.getAllTransactions(month, year).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        Toast.makeText(context, "Memuat data...", Toast.LENGTH_SHORT).show()
                    }
                    is ApiResponse.Success -> {
                        val dailySummaries = response.data
                        if (dailySummaries.isNullOrEmpty()) {
                            Toast.makeText(context, "Tidak ada data untuk bulan ini.", Toast.LENGTH_SHORT).show()
                            updateHarianTab(emptyList())
                        } else {
                            // Panggil fungsi pemrosesan yang baru
                            val harianData = processSummariesToHarian(dailySummaries)
                            updateHarianTab(harianData)
                        }
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, response.errorMessage, Toast.LENGTH_LONG).show()
                        updateHarianTab(emptyList())
                    }
                }
            }
        }
    }

    private fun updateHarianTab(data: List<RangkumanHarian>) {
        viewPager.post {
            val harianFragment = childFragmentManager.findFragmentByTag("f0") as? TabHarianFragment
            harianFragment?.submitHarianData(data)
        }
    }

    private fun updateMonthYearText() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        txtMonthYear.text = sdf.format(calendar.time)
    }

    /**
     * FUNGSI INI DITULIS ULANG
     * Mengubah List<DailySummaryResponse> dari API menjadi List<RangkumanHarian> untuk UI.
     * @param summaries Data yang diterima langsung dari API.
     * @return List data yang siap ditampilkan oleh HarianAdapter.
     */
    private fun processSummariesToHarian(summaries: List<DailySummaryResponse>): List<RangkumanHarian> {
        return summaries.map { summary ->
            // Mengubah format tanggal dari "yyyy-MM-dd" ke "E" (nama hari) dan "dd/MM/yyyy"
            val sdfSource = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdfSource.parse(summary.date ?: "")

            val dayFormat = SimpleDateFormat("E", Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Memetakan setiap transaksi di dalam summary ke model DataHarian
            val detailHarian = summary.transactions?.map { trx ->
                val sdfTransaction = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val detailDate = sdfTransaction.parse(trx.createdAt ?: "")
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                DataHarian(
                    waktu = if (detailDate != null) timeFormat.format(detailDate) else "--:--",
                    jenis = trx.description ?: "Tanpa Deskripsi",
                    // Mengubah cara mengambil kategori, karena sekarang hanya string
                    media = (trx.category as? String) ?: "Lainnya",
                    pendapatan = if (trx.type == "income") "Rp. ${trx.amount}" else "Rp. 0",
                    pengeluaran = if (trx.type == "expense") "Rp. ${trx.amount}" else "Rp. 0"
                )
            } ?: emptyList()

            // Membuat objek RangkumanHarian dari data summary
            RangkumanHarian(
                hari = if (date != null) dayFormat.format(date) else "---",
                tanggal = if (date != null) dateFormat.format(date) else "--/--/----",
                // Mengambil total pendapatan dan pengeluaran langsung dari summary
                totalPendapatan = "Rp. ${summary.income ?: 0}",
                totalPengeluaran = "Rp. ${summary.expense ?: 0}",
                detail = detailHarian
            )
        }.sortedByDescending { it.tanggal } // Mengurutkan hasil akhir berdasarkan tanggal
    }
}
