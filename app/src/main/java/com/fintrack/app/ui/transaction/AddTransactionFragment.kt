package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fintrack.app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {

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

        // Inisialisasi semua view dari layout
        initializeViews(view)

        // Atur tanggal yang dipilih saat ini
        updateSelectedDate(calendar.timeInMillis)

        // Setup listener untuk CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            updateSelectedDate(selectedCalendar.timeInMillis)
        }

        // Setup listener untuk pilihan tipe (pendapatan/pengeluaran)
        radioGroupTipe.setOnCheckedChangeListener { _, checkedId ->
            updateCategoryAdapter(checkedId)
        }

        // Setup listener untuk tombol simpan
        btnSimpan.setOnClickListener {
            simpanTransaksi()
        }

        // Panggil untuk pertama kali untuk mengisi adapter kategori
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
        // Data ini seharusnya datang dari API atau database Anda
        val categories = if (checkedId == R.id.radio_pendapatan) {
            arrayOf("Gaji", "Bonus", "Hadiah", "Investasi", "Lainnya")
        } else { // Pengeluaran
            arrayOf("Makanan", "Transportasi", "Belanja", "Hiburan", "Tagihan", "Kesehatan", "Lainnya")
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        autoCompleteKategori.setAdapter(adapter)
        // Reset pilihan kategori saat tipe berubah
        autoCompleteKategori.setText("", false)
    }

    private fun simpanTransaksi() {
        val tipe = if (radioGroupTipe.checkedRadioButtonId == R.id.radio_pendapatan) "income" else "expense"
        val total = etTotal.text.toString()
        val kategori = autoCompleteKategori.text.toString()
        val catatan = etCatatan.text.toString()

        // Validasi input sederhana
        if (selectedDate.isEmpty() || total.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(context, "Harap lengkapi tipe, tanggal, total, dan kategori.", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan data yang akan disimpan (ganti dengan logika API call)
        val dataToSave = "Tipe: $tipe\nTanggal: $selectedDate\nTotal: $total\nKategori: $kategori\nCatatan: $catatan"
        Toast.makeText(context, "Menyimpan data:\n$dataToSave", Toast.LENGTH_LONG).show()

        // TODO: Panggil ViewModel atau Repository untuk menyimpan data transaksi ke API
        // Contoh: viewModel.addTransaction(type, selectedDate, total, kategori, catatan)

        // Setelah berhasil, kembali ke halaman sebelumnya
        // parentFragmentManager.popBackStack()
    }
}
