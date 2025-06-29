package com.fintrack.app.ui.closeBook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fintrack.app.R
import com.fintrack.app.data.BudgetRepository
import com.fintrack.app.data.TransactionRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.TransactionPayload
import com.fintrack.app.data.response.DailySummaryResponse
import com.fintrack.app.data.response.GetMonthlyBudgetResponse
import com.fintrack.app.databinding.FragmentTabTutupBukuBinding
import com.fintrack.app.ui.manageBudget.BudgetItem
import com.fintrack.app.ui.manageBudget.ManageBudgetFragment
import com.fintrack.app.utils.CategoryIconMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TabTutupBukuFragment : Fragment() {

    private var _binding: FragmentTabTutupBukuBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private lateinit var budgetAdapter: BudgetAdapter
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabTutupBukuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupActionButtons()

        // Ambil data untuk bulan dan tahun saat ini
        val year = calendar.get(Calendar.YEAR)
        val monthForBudget = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        // Calendar.MONTH is 0-based, jadi perlu ditambah 1 untuk API
        val monthForTransactions = calendar.get(Calendar.MONTH) + 1

        fetchMonthlyBudget(monthForBudget)
        // Panggil fungsi baru untuk mengambil ringkasan transaksi
        fetchTransactionSummary(year, monthForTransactions)
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter()
        binding.recyclerViewKategori.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = budgetAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchMonthlyBudget(month: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            budgetRepository.getBudgetMonthly(month).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        // Handle loading state if needed
                    }
                    is ApiResponse.Success -> {
                        handleSuccessfulResponse(response.data)
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, response.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    /**
     * FUNGSI BARU: Untuk mengambil ringkasan transaksi (pemasukan & pengeluaran) dari repository
     * dan memicu pembaruan UI.
     */
    private fun fetchTransactionSummary(year: Int, month: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            transactionRepository.getAllTransactions(month, year).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        // Anda bisa menampilkan loading indicator di card aset jika diinginkan
                    }
                    is ApiResponse.Success -> {
                        // Asumsi DailySummaryResponse memiliki properti totalIncome dan totalExpense.
                        // Sesuaikan nama properti ini jika berbeda di data class Anda.
                        val totalIncome = response.data.sumOf { it.income ?: 0.0 }
                        val totalExpense = response.data.sumOf { it.expense ?: 0.0 }
                        updateAssetCard(totalIncome, totalExpense)
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, "Gagal memuat ringkasan aset: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        // Set nilai default ke 0 jika gagal memuat
                        updateAssetCard(0.0, 0.0)
                    }
                }
            }
        }
    }

    private fun handleSuccessfulResponse(data: GetMonthlyBudgetResponse) {
        updateBudgetCard(data.totalBudget?.toDouble() ?: 0.0, data.totalExpense?.toDouble() ?: 0.0)

        val categoryList = data.remainingBudgetList?.mapNotNull { budgetDetail ->
            budgetDetail?.let {
                val categoryName = (it.categoryName as? String) ?: "Tanpa Kategori"

                BudgetItem(
                    id = it.id ?: "",
                    name = categoryName,
                    amount = it.amountLimit?.toDouble() ?: 0.0,
                    used = it.spentAmount?.toDouble() ?: 0.0,
                    iconResId = CategoryIconMapper.getIconForCategory(categoryName)
                )
            }
        } ?: emptyList()

        budgetAdapter.updateData(categoryList)
    }

    private fun updateBudgetCard(totalBudget: Double, totalUsed: Double) {
        val percentage = if (totalBudget > 0) (totalUsed / totalBudget * 100).toInt() else 0
        binding.progressAnggaran.progress = percentage
        binding.textPersentaseAnggaran.text = "$percentage%"

        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        binding.textTotalAmount.text = format.format(totalBudget)
    }

    /**
     * FUNGSI BARU: Untuk memperbarui UI pada card aset dengan data pemasukan dan pengeluaran.
     */
    private fun updateAssetCard(totalIncome: Double, totalExpense: Double) {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0

        binding.textIncomeAmount.text = format.format(totalIncome)
        binding.textExpenseAmount.text = format.format(totalExpense)

        val netAmount = totalIncome - totalExpense
        binding.textNetAmount.text = format.format(netAmount)
    }

    private fun setupActionButtons() {
        binding.buttonUbahKategori.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageBudgetFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.buttonExportLaporan.setOnClickListener {
            exportTransactionHistory()
        }
    }

    private fun exportTransactionHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            transactionRepository.exportTransactions().collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        binding.buttonExportLaporan.isEnabled = false
                        Toast.makeText(requireContext(), "Memulai proses export...", Toast.LENGTH_SHORT).show()
                    }
                    is ApiResponse.Success -> {
                        binding.buttonExportLaporan.isEnabled = true
                        Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_LONG).show()
                    }
                    is ApiResponse.Error -> {
                        binding.buttonExportLaporan.isEnabled = true
                        Toast.makeText(requireContext(), "Export gagal: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
