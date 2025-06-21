package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.TransactionRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.CreateTransactionRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private val calendar = Calendar.getInstance()
    private lateinit var calendarView: CalendarView
    private lateinit var etTotal: EditText
    private lateinit var etCatatan: EditText
    private lateinit var radioGroupTipe: RadioGroup
    private lateinit var autoCompleteKategori: AutoCompleteTextView
    private lateinit var btnSimpan: Button

    private var selectedDate: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)

        // -- PERUBAHAN DI SINI --
        // Atur agar tanggal di masa depan tidak bisa dipilih.
        calendarView.maxDate = System.currentTimeMillis()
        // ----------------------

        updateSelectedDate(calendar.timeInMillis)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            updateSelectedDate(selectedCalendar.timeInMillis)
        }

        radioGroupTipe.setOnCheckedChangeListener { _, checkedId ->
            updateCategoryAdapter(checkedId)
        }

        btnSimpan.setOnClickListener {
            simpanTransaksi()
        }

        updateCategoryAdapter(radioGroupTipe.checkedRadioButtonId)
    }

    private fun initializeViews(view: View) {
        calendarView = view.findViewById(R.id.calendar_view_tanggal)
        etTotal = view.findViewById(R.id.edittext_total)
        etCatatan = view.findViewById(R.id.edittext_catatan)
        radioGroupTipe = view.findViewById(R.id.radiogroup_tipe)
        autoCompleteKategori = view.findViewById(R.id.autocompletetext_kategori)
        btnSimpan = view.findViewById(R.id.button_simpan_transaksi)
    }

    private fun updateSelectedDate(dateInMillis: Long) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = sdf.format(dateInMillis)
    }

    private fun updateCategoryAdapter(checkedId: Int) {
        val categories = if (checkedId == R.id.radio_pendapatan) {
            arrayOf("Gaji", "Bonus", "Hadiah", "Investasi", "Lainnya")
        } else {
            arrayOf("Makanan", "Transportasi", "Belanja", "Hiburan", "Tagihan", "Kesehatan", "Lainnya")
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        autoCompleteKategori.setAdapter(adapter)
        autoCompleteKategori.setText("", false)
    }

    private fun simpanTransaksi() {
        val tipe = if (radioGroupTipe.checkedRadioButtonId == R.id.radio_pendapatan) "income" else "expense"
        val totalStr = etTotal.text.toString()
        val kategori = autoCompleteKategori.text.toString()
        val catatan = etCatatan.text.toString()

        if (totalStr.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(context, "Total dan Kategori tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        val totalDouble = totalStr.toDoubleOrNull()
        if (totalDouble == null) {
            Toast.makeText(context, "Format isian Total tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        btnSimpan.isEnabled = false

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
                        // Loading state
                    }
                    is ApiResponse.Success -> {
                        Toast.makeText(context, response.data.message, Toast.LENGTH_LONG).show()
                        parentFragmentManager.popBackStack()
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, "Error: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        Log.e("AddTransactionFragment", "Error: ${response.errorMessage}")
                        btnSimpan.isEnabled = true
                    }
                }
            }
        }
    }
}