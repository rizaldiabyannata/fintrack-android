package com.fintrack.app.ui.transaction

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialDate()
        setupListeners()
        updateCategoryAdapter(binding.radiogroupTipe.checkedRadioButtonId)
    }

    private fun setupInitialDate() {
        // Set tanggal hari ini sebagai default saat fragment dibuat
        updateSelectedDate(calendar.time)
    }

    private fun setupListeners() {
        binding.buttonKembaliTambahAnggaran.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.edittextTanggal.setOnClickListener {
            showDatePickerDialog()
        }

        binding.radiogroupTipe.setOnCheckedChangeListener { _, checkedId ->
            updateCategoryAdapter(checkedId)
        }

        binding.buttonSimpanTransaksi.setOnClickListener {
            simpanTransaksi()
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateSelectedDate(calendar.time)
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Batasi agar tidak bisa memilih tanggal di masa depan
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    private fun updateSelectedDate(date: Date) {
        // Format untuk dikirim ke API (contoh: 2023-10-27)
        val sdfApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = sdfApi.format(date)

        // Format untuk ditampilkan di UI (contoh: 27 Oktober 2023)
        val sdfUi = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.edittextTanggal.setText(sdfUi.format(date))
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

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateObject: Date = try {
            sdf.parse(selectedDate) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        val requestPayload = CreateTransactionRequest(
            type = tipe,
            category = kategori,
            amount = totalDouble,
            description = catatan,
            date = dateObject
        )

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
