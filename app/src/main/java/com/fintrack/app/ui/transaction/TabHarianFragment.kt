package com.fintrack.app.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R
import com.fintrack.app.ui.addBudget.AddBudgetFragment
import com.fintrack.app.ui.transaction.AddTransactionFragment

class TabHarianFragment : Fragment(R.layout.fragment_tab_harian) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HarianAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dan Tombol Tambah
        recyclerView = view.findViewById(R.id.recyclerViewHarian)
        val btnTambah: ImageButton = view.findViewById(R.id.btnTambahHarian)

        setupRecyclerView()

        // Setup listener untuk tombol tambah
        btnTambah.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddTransactionFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        adapter = HarianAdapter(emptyList()) // Mulai dengan list kosong
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    /**
     * Fungsi publik untuk menerima data dari pa    rent fragment (TransaksiFragment).
     * @param data List data RangkumanHarian yang sudah diproses.
     */
    fun submitHarianData(data: List<RangkumanHarian>) {
        // Memastikan adapter sudah diinisialisasi sebelum digunakan
        if (::adapter.isInitialized) {
            adapter.updateData(data)
        }
    }
}
