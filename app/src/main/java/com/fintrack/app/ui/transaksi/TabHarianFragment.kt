package com.fintrack.app.ui.transaksi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class TabHarianFragment : Fragment(R.layout.fragment_tab_harian) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HarianAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewHarian)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            DataHarian("Wed", "16/05/25", "Rp. 100.100.123.456,00", "Makan Siang dan Makan-Makan", "ShopeePay Selalu di Depan tapi di belakang juga ada sih wkwkwkwk dahlah capek", "Rp. 50.000.000.000,00"),
            DataHarian("Thu", "17/05/25", "Rp. 200.000,00", "Internet", "Transfer Bank", "Rp. 75.000,00"),
            DataHarian("Fri", "18/05/25", "Rp. 100.000,00", "Belanja", "Cash", "Rp. 40.000,00"),
            DataHarian("Sat", "19/05/25", "Rp. 150.000,00", "Transportasi", "OVO", "Rp. 60.000,00"),
            DataHarian("Sun", "20/05/25", "Rp. 90.000,00", "Donasi", "Gopay", "Rp. 30.000,00"),
            DataHarian("Mon", "21/05/25", "Rp. 300.000,00", "Tagihan", "ShopeePay", "Rp. 150.000,00"),
            DataHarian("Tue", "22/05/25", "Rp. 250.000,00", "Langganan", "Dana", "Rp. 100.000,00"),
            DataHarian("Wed", "23/05/25", "Rp. 180.000,00", "Makan Malam", "Cash", "Rp. 80.000,00"),
            DataHarian("Thu", "24/05/25", "Rp. 220.000,00", "Belanja Online", "ShopeePay", "Rp. 120.000,00")
        )

        adapter = HarianAdapter(dummyData)
        recyclerView.adapter = adapter
    }
}