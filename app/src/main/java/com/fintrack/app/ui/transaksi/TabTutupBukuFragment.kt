package com.fintrack.app.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fintrack.app.R
import com.google.android.material.button.MaterialButton

class TabTutupBukuFragment : Fragment() {

    private lateinit var progressAnggaran: ProgressBar
    private lateinit var textPersentaseAnggaran: TextView
    private lateinit var buttonTambahKategori: Button
    private lateinit var buttonExportLaporan: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_tutup_buku, container, false)

        progressAnggaran = view.findViewById(R.id.progress_anggaran)
        textPersentaseAnggaran = view.findViewById(R.id.text_persentase_anggaran)
        buttonTambahKategori = view.findViewById(R.id.button_tambah_kategori)
        buttonExportLaporan = view.findViewById(R.id.button_export_laporan)

        // Simulasi data dummy
        val progressValue = 60
        progressAnggaran.progress = progressValue
        textPersentaseAnggaran.text = "$progressValue%"

        val buttonTambahKategori = view.findViewById<Button>(R.id.button_tambah_kategori)
        buttonTambahKategori.setOnClickListener {
            val intent = Intent(requireContext(), TambahAnggaranActivity::class.java)
            startActivity(intent)
        }


        buttonExportLaporan.setOnClickListener {
            Toast.makeText(requireContext(), "Export laporan via email", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
