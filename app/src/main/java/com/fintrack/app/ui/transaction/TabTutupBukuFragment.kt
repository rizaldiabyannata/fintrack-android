package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R
import com.fintrack.app.ui.transaction.KategoriAnggaranAdapter
import com.fintrack.app.ui.transaction.BudgetCategory

class TabTutupBukuFragment : Fragment() {

    private lateinit var recyclerViewKategori: RecyclerView
    private lateinit var kategoriAnggaranAdapter: KategoriAnggaranAdapter
    private lateinit var textTotalAnggaran: TextView
    private lateinit var progressAnggaran: ProgressBar
    private lateinit var textPersentaseAnggaran: TextView
    private lateinit var btnTambahKategori: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_tutup_buku, container, false)

        textTotalAnggaran = view.findViewById(R.id.text_total_anggaran)
        progressAnggaran = view.findViewById(R.id.progress_anggaran)
        textPersentaseAnggaran = view.findViewById(R.id.text_persentase_anggaran)

        val totalAnggaran = 3000000.0
        val pengeluaranSaatIni = 1800000.0
        val persentase = ((pengeluaranSaatIni / totalAnggaran) * 100).toInt()

        textTotalAnggaran.text = "Rp. ${String.format("%,.0f", totalAnggaran).replace(",", ".")},00"
        progressAnggaran.progress = persentase
        textPersentaseAnggaran.text = "$persentase%"

        recyclerViewKategori = view.findViewById(R.id.recycler_view_kategori)
        recyclerViewKategori.layoutManager = LinearLayoutManager(context)

        val kategoriData = listOf(
            BudgetCategory("Makanan", 1500000.0, R.drawable.ic_restaurant),
            BudgetCategory("Daily Life", 750000.0, R.drawable.ic_list),
            BudgetCategory("Internet", 500000.0, R.drawable.ic_list),
            BudgetCategory("Transportasi", 250000.0, R.drawable.ic_list)
        )

        kategoriAnggaranAdapter = KategoriAnggaranAdapter(kategoriData)
        recyclerViewKategori.adapter = kategoriAnggaranAdapter

        btnTambahKategori = view.findViewById(R.id.button_tambah_kategori)
        btnTambahKategori.setOnClickListener {
            Toast.makeText(requireContext(), "Tombol Tambah Kategori diklik!", Toast.LENGTH_SHORT).show()

            // ⬇️ GUNAKAN FUNGSI MANUAL DI TransaksiFragment
            (parentFragment as? TransaksiFragment)?.bukaTambahAnggaranFragment()
        }

        return view
    }
}
