package com.fintrack.app.ui.transaction

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
import com.fintrack.app.data.TransactionRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.CreateTransactionRequest
import com.fintrack.app.databinding.FragmentAddTransactionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        updateCategoryAdapter(binding.radiogroupTipe.checkedRadioButtonId)
    }

    private fun setupListeners() {
        binding.buttonKembaliTambahAnggaran.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.radiogroupTipe.setOnCheckedChangeListener { _, checkedId ->
            updateCategoryAdapter(checkedId)
        }

        binding.buttonSimpanTransaksi.setOnClickListener {
            simpanTransaksi()
        }
    }

    private fun updateCategoryAdapter(checkedId: Int) {
        val categories = if (checkedId == R.id.radio_pendapatan) {
            resources.getStringArray(R.array.income_categories)
        } else {
            resources.getStringArray(R.array.expense_categories)
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.autocompletetextKategori.setAdapter(adapter)
        binding.autocompletetextKategori.setText("", false)
    }

    private fun simpanTransaksi() {
        val tipe = if (binding.radiogroupTipe.checkedRadioButtonId == R.id.radio_pendapatan) "income" else "expense"
        val totalStr = binding.edittextTotal.text.toString()
        val kategori = binding.autocompletetextKategori.text.toString()
        val catatan = binding.edittextCatatan.text.toString()

        if (totalStr.isBlank() || kategori.isBlank()) {
            Toast.makeText(context, "Total dan Kategori tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        val totalDouble = totalStr.toDoubleOrNull()
        if (totalDouble == null) {
            Toast.makeText(context, "Format isian Total tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.buttonSimpanTransaksi.isEnabled = false

        // Payload tidak lagi menyertakan 'date'
        val requestPayload = CreateTransactionRequest(
            type = tipe,
            category = kategori,
            amount = totalDouble,
            description = catatan
        )

        // Pastikan data class CreateTransactionRequest juga tidak memiliki field 'date'
        // atau field tersebut nullable.

        viewLifecycleOwner.lifecycleScope.launch {
            transactionRepository.postCreateTransaction(requestPayload).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        // Tambahkan indikator loading jika perlu
                    }
                    is ApiResponse.Success -> {
                        Toast.makeText(context, response.data.message, Toast.LENGTH_LONG).show()
                        parentFragmentManager.popBackStack()
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, "Error: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        Log.e("AddTransactionFragment", "Error: ${response.errorMessage}")
                        binding.buttonSimpanTransaksi.isEnabled = true
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
