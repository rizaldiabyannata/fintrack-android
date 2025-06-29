package com.fintrack.app.ui.addBudget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.BudgetRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.BudgetPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.databinding.FragmentAddBudgetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddBudgetFragment : Fragment() {

    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var budgetRepository: BudgetRepository

    private var budgetIdToEdit: String? = null
    private var isEditMode = false
    private var categoryIdForUpdate: String? = null

    companion object {
        const val ARG_BUDGET_ID = "budget_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            budgetIdToEdit = it.getString(ARG_BUDGET_ID)
            isEditMode = budgetIdToEdit != null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        updateCategoryAdapter()

        if (isEditMode) {
            setupEditMode()
            loadBudgetData()
        }
    }

    private fun setupEditMode() {
        binding.titleTambahAnggaran.text = "Edit Anggaran"
        binding.buttonSimpanAnggaran.text = "Update Anggaran"
        // Tampilkan tombol hapus
        binding.buttonHapusAnggaran.isVisible = true
    }

    private fun loadBudgetData() {
        budgetIdToEdit?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                budgetRepository.getBudgetById(id).collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            val budget = response.data
                            categoryIdForUpdate = budget.category?.id
                            binding.autocompletetextKategori.setText(budget.category?.name, false)
                            binding.edittextJumlahAnggaran.setText(budget.amountLimit.toString())
                            binding.tilKategori.isEnabled = false
                        }
                        is ApiResponse.Error -> {
                            Toast.makeText(context, "Gagal memuat data: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
                        }
                        is ApiResponse.Loading -> { /* show loading if needed */ }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonKembaliTambahAnggaran.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.buttonSimpanAnggaran.setOnClickListener {
            simpanAnggaran()
        }

        // DITAMBAHKAN: Listener untuk tombol hapus
        binding.buttonHapusAnggaran.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun updateCategoryAdapter() {
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.autocompletetextKategori.setAdapter(adapter)
    }

    private fun simpanAnggaran() {
        val namaKategori = binding.autocompletetextKategori.text.toString().trim()
        val jumlahAnggaranStr = binding.edittextJumlahAnggaran.text.toString().trim()

        if (namaKategori.isEmpty() || jumlahAnggaranStr.isEmpty()) {
            Toast.makeText(context, "Kategori dan Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlahAnggaran = jumlahAnggaranStr.toDoubleOrNull()
        if (jumlahAnggaran == null || jumlahAnggaran <= 0) {
            Toast.makeText(context, "Jumlah Anggaran tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryValue = if (isEditMode) categoryIdForUpdate ?: "" else namaKategori
        if (isEditMode && categoryValue.isEmpty()) {
            Toast.makeText(context, "ID Kategori tidak ditemukan, tidak bisa update.", Toast.LENGTH_LONG).show()
            return
        }

        val requestPayload = BudgetPayload(
            category = categoryValue,
            amountLimit = jumlahAnggaran
        )

        if (isEditMode) {
            updateExistingBudget(requestPayload)
        } else {
            createNewBudget(requestPayload)
        }
    }

    // DITAMBAHKAN: Dialog konfirmasi dan fungsi untuk memanggil API hapus
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Anggaran")
            .setMessage("Apakah Anda yakin ingin menghapus anggaran ini?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteBudget()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteBudget() {
        budgetIdToEdit?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                budgetRepository.deleteBudget(id).collect { response ->
                    handleApiResponse(response, "hapus")
                }
            }
        }
    }

    private fun createNewBudget(payload: BudgetPayload) {
        viewLifecycleOwner.lifecycleScope.launch {
            budgetRepository.createBudget(payload).collect { response ->
                handleApiResponse(response, "simpan")
            }
        }
    }

    private fun updateExistingBudget(payload: BudgetPayload) {
        budgetIdToEdit?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                budgetRepository.updateBudget(id, payload).collect { response ->
                    handleApiResponse(response, "update")
                }
            }
        }
    }

    private fun handleApiResponse(response: ApiResponse<BaseResponse>, action: String) {
        when (response) {
            is ApiResponse.Loading -> showLoading(true)
            is ApiResponse.Success -> {
                showLoading(false)
                Toast.makeText(context, response.data.message, Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }
            is ApiResponse.Error -> {
                showLoading(false)
                Toast.makeText(context, "Gagal $action: ${response.errorMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSimpanAnggaran.isEnabled = !isLoading
        binding.buttonHapusAnggaran.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
