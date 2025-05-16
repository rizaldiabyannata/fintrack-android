package com.fintrack.app.ui.transaksi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class TabBulananFragment : Fragment(R.layout.fragment_tab_bulanan) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BulananAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewBulanan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            DataBulanan("May", "16/05/25", "Rp. 100.100.123.456,00", "Rp. 50.000.000.000,00"),
            DataBulanan("Jun", "17/06/25", "Rp. 200.000,00", "Rp. 75.000,00"),
            DataBulanan("Jul", "18/07/25", "Rp. 100.000,00", "Rp. 40.000,00"),
            DataBulanan("Aug", "19/08/25", "Rp. 150.000,00", "Rp. 60.000,00"),
            DataBulanan("Sep", "20/09/25", "Rp. 90.000,00", "Rp. 30.000,00"),
            DataBulanan("Oct", "21/10/25", "Rp. 300.000,00", "Rp. 150.000,00"),
            DataBulanan("Nov", "22/11/25", "Rp. 250.000,00", "Rp. 100.000,00"),
            DataBulanan("Dec", "23/12/25", "Rp. 180.000,00", "Rp. 80.000,00"),
            DataBulanan("Jan", "24/01/26", "Rp. 220.000,00", "Rp. 120.000,00")
        )

        adapter = BulananAdapter(dummyData)
        recyclerView.adapter = adapter
    }
}