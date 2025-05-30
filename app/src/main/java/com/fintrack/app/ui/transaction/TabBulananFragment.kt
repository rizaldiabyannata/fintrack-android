//package com.fintrack.app.ui.transaction
//
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.fintrack.app.R
//
//class TabBulananFragment : Fragment(R.layout.fragment_tab_bulanan) {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: BulananAdapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        recyclerView = view.findViewById(R.id.recyclerViewBulanan)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Dummy data detail untuk setiap bulan
//        val detailMei = listOf(
//            DataBulanan("01", "Rp. 1.000.000.000.000.000,00", "Rp. 200.000.000.000.000,00"),
//            DataBulanan("02", "Rp. 500.000", "Rp. 100.000"),
//            DataBulanan("03", "Rp. 2.000.000", "Rp. 1.000.000"),
//            DataBulanan("04", "Rp. 1.000.000.000.000.000,00", "Rp. 200.000.000.000.000,00"),
//            DataBulanan("05", "Rp. 500.000", "Rp. 100.000"),
//            DataBulanan("06", "Rp. 2.000.000", "Rp. 1.000.000"),
//            DataBulanan("04", "Rp. 1.000.000.000.000.000,00", "Rp. 200.000.000.000.000,00"),
//            DataBulanan("05", "Rp. 500.000", "Rp. 100.000"),
//            DataBulanan("06", "Rp. 2.000.000", "Rp. 1.000.000")
//        )
//        val detailJun = listOf(
//            DataBulanan("01", "Rp. 1.200.000", "Rp. 300.000"),
//            DataBulanan("02", "Rp. 700.000", "Rp. 50.000")
//        )
//        val detailJul = listOf(
//            DataBulanan("04", "Rp. 800.000", "Rp. 100.000"),
//            DataBulanan("05", "Rp. 900.000", "Rp. 200.000")
//        )
//        val detailAgu = listOf(
//            DataBulanan("06", "Rp. 1.500.000", "Rp. 300.000"),
//            DataBulanan("07", "Rp. 1.000.000", "Rp. 250.000")
//        )
//        val detailSep = listOf(
//            DataBulanan("08", "Rp. 2.000.000", "Rp. 1.000.000"),
//            DataBulanan("09", "Rp. 1.000.000", "Rp. 200.000")
//        )
//        val detailOkt = listOf(
//            DataBulanan("10", "Rp. 1.800.000", "Rp. 900.000"),
//            DataBulanan("11", "Rp. 1.200.000", "Rp. 300.000")
//        )
//        val detailNov = listOf(
//            DataBulanan("12", "Rp. 2.500.000", "Rp. 1.000.000"),
//            DataBulanan("13", "Rp. 1.000.000", "Rp. 500.000")
//        )
//        val detailDes = listOf(
//            DataBulanan("14", "Rp. 2.200.000", "Rp. 800.000"),
//            DataBulanan("15", "Rp. 1.800.000", "Rp. 600.000")
//        )
//
//        val dummySummaryData = listOf(
//            RangkumanBulanan("May", "2025", "Rp. 3.500.000.000.000.000.000,00", "Rp. 1.300.000.000.000.000.000,00", detailMei),
//            RangkumanBulanan("Jun", "2025", "Rp. 1.900.000", "Rp. 350.000", detailJun),
//            RangkumanBulanan("Jul", "2025", "Rp. 1.700.000", "Rp. 300.000", detailJul),
//            RangkumanBulanan("Aug", "2025", "Rp. 2.500.000", "Rp. 550.000", detailAgu),
//            RangkumanBulanan("Sep", "2025", "Rp. 3.000.000", "Rp. 1.200.000", detailSep),
//            RangkumanBulanan("Oct", "2025", "Rp. 3.000.000", "Rp. 1.200.000", detailOkt),
//            RangkumanBulanan("Nov", "2025", "Rp. 3.500.000", "Rp. 1.500.000", detailNov),
//            RangkumanBulanan("Dec", "2025", "Rp. 4.000.000", "Rp. 1.400.000", detailDes)
//        )
//
//
//        adapter = BulananAdapter(dummySummaryData)
//        recyclerView.adapter = adapter
//    }
//}
