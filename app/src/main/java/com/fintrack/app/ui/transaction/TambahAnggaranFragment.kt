package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class TambahAnggaranFragment : Fragment() {

    private lateinit var recyclerViewDaftarAnggaran: RecyclerView
    private lateinit var daftarAnggaranAdapter: DaftarAnggaranAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tambah_anggaran, container, false)

        val buttonKembali: ImageView = view.findViewById(R.id.button_kembali_anggaran)
        buttonKembali.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val buttonTambahAnggaranToolbar: ImageView = view.findViewById(R.id.button_tambah_anggaran)
        buttonTambahAnggaranToolbar.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TambahAnggaranBaruFragment())
                .addToBackStack(null)
                .commit()
        }

        recyclerViewDaftarAnggaran = view.findViewById(R.id.recycler_view_daftar_anggaran)
        recyclerViewDaftarAnggaran.layoutManager = LinearLayoutManager(context)

        val daftarAnggaranData = listOf(
            BudgetItem("Kehidupan Sosial", 500000.0, R.drawable.ic_people),
            BudgetItem("Makanan", 1500000.0, R.drawable.ic_restaurant),
            BudgetItem("Internet", 250000.0, R.drawable.ic_globe),
            BudgetItem("Hiburan", 300000.0, R.drawable.ic_globe)
        )

        daftarAnggaranAdapter = DaftarAnggaranAdapter(daftarAnggaranData)
        recyclerViewDaftarAnggaran.adapter = daftarAnggaranAdapter

        daftarAnggaranAdapter.onEditClickListener = { budgetItem ->
            Toast.makeText(context, "Edit ${budgetItem.categoryName} diklik!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
