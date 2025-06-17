package com.fintrack.app.ui.addBudget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fintrack.app.databinding.FragmentAddBudgetBinding

class AddBudgetFragment : Fragment() {

    // Deklarasi variabel binding untuk mengakses view
    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout menggunakan ViewBinding
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menambahkan aksi klik untuk tombol kembali
        binding.buttonKembaliTambahAnggaran.setOnClickListener {
            // Kembali ke fragment atau activity sebelumnya
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Menambahkan aksi klik untuk tombol simpan
        binding.buttonSimpanAnggaran.setOnClickListener {
            // Mengambil data dari EditText
            val namaKategori = binding.edittextNamaKategori.text.toString().trim()
            val jumlahAnggaran = binding.edittextJumlahAnggaran.text.toString().trim()

            // Validasi sederhana: pastikan input tidak kosong
            if (namaKategori.isEmpty()) {
                binding.edittextNamaKategori.error = "Nama kategori tidak boleh kosong"
                return@setOnClickListener
            }

            if (jumlahAnggaran.isEmpty()) {
                binding.edittextJumlahAnggaran.error = "Jumlah anggaran tidak boleh kosong"
                return@setOnClickListener
            }

            // TODO: Implementasikan logika untuk menyimpan data ke database atau ViewModel
            // Untuk saat ini, kita hanya akan menampilkan Toast
            val pesan = "Anggaran '$namaKategori' sebesar Rp$jumlahAnggaran berhasil disimpan"
            Toast.makeText(requireContext(), pesan, Toast.LENGTH_LONG).show()

            // Setelah menyimpan, kembali ke halaman sebelumnya
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Membersihkan referensi binding untuk menghindari memory leak
        _binding = null
    }
}