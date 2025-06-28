package com.fintrack.app.ui.addBudget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.BudgetRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.BudgetPayload
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
        // Panggil untuk mengisi adapter dengan kategori pengeluaran
        updateCategoryAdapter()
    }

    private fun setupListeners() {
        binding.buttonKembaliTambahAnggaran.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.buttonSimpanAnggaran.setOnClickListener {
            simpanAnggaran()
        }
    }

    private fun updateCategoryAdapter() {
        // Selalu gunakan daftar kategori pengeluaran
        val categories = resources.getStringArray(R.array.expense_categories)

        // Buat adapter dan set ke AutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.autocompletetextKategori.setAdapter(adapter)
    }

    private fun simpanAnggaran() {
        val namaKategori = binding.autocompletetextKategori.text.toString().trim()
        val jumlahAnggaranStr = binding.edittextJumlahAnggaran.text.toString().trim()

        if (namaKategori.isEmpty()) {
            binding.tilKategori.error = "Kategori tidak boleh kosong"
            return
        } else {
            binding.tilKategori.error = null
        }

        if (jumlahAnggaranStr.isEmpty()) {
            binding.tilJumlahAnggaran.error = "Jumlah anggaran tidak boleh kosong"
            return
        } else {
            binding.tilJumlahAnggaran.error = null
        }

        val jumlahAnggaran = jumlahAnggaranStr.toDoubleOrNull()
        if (jumlahAnggaran == null || jumlahAnggaran <= 0) {
            binding.tilJumlahAnggaran.error = "Jumlah anggaran tidak valid"
            return
        }

        // Buat payload untuk dikirim ke API
        val requestPayload = BudgetPayload(
            category = namaKategori,
            amountLimit = jumlahAnggaran
        )

        // Gunakan coroutine untuk memanggil repository
        viewLifecycleOwner.lifecycleScope.launch {
            budgetRepository.createBudget(requestPayload).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        showLoading(true)
                    }
                    is ApiResponse.Success -> {
                        showLoading(false)
                        Toast.makeText(context, response.data.message, Toast.LENGTH_LONG).show()
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        Toast.makeText(context, "Error: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        Log.e("AddBudgetFragment", "API Error: ${response.errorMessage}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSimpanAnggaran.isEnabled = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
