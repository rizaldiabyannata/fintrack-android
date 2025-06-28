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
import com.fintrack.app.data.TransactionRepository // <-- DITAMBAHKAN
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.TransactionPayload // <-- DITAMBAHKAN
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

    // DITAMBAHKAN: Inject TransactionRepository
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

        val monthString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        fetchMonthlyBudget(monthString)
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

    private fun handleSuccessfulResponse(data: GetMonthlyBudgetResponse) {
        updateBudgetCard(data.totalBudget?.toDouble() ?: 0.0, data.totalExpense?.toDouble() ?: 0.0)

        Log.d("TabTutupBukuFragment", "Data yang diterima: $data")

        val categoryList = data.remainingBudgetList?.mapNotNull { budgetDetail ->
            budgetDetail?.let {
                val categoryName = (it.categoryName as? String) ?: "Tanpa Kategori"
                BudgetItem(
                    id= it.id ?: "",
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

    private fun setupActionButtons() {
        binding.buttonUbahKategori.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageBudgetFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.buttonExportLaporan.setOnClickListener {
            // Memanggil fungsi export saat tombol di klik
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
                        // Tampilkan pesan sukses dari backend
                        Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_LONG).show()
                    }
                    is ApiResponse.Error -> {
                        binding.buttonExportLaporan.isEnabled = true
                        // Tampilkan pesan error
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
