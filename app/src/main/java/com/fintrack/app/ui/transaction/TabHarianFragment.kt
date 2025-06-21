package com.fintrack.app.ui.transaction

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

        // Dummy data detail untuk setiap hari
        val detailSenin = listOf(
            DataHarian("01:00", "Makan", "ShopeePay", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("04:00", "Utang", "Bank BRI", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("05:15", "Furnitur", "ShopeePay", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("18:30", "Makan Siang dan Makan-Makan", "ShopeePay Selalu di Depan tapi di belakang juga ada sih wkwkwkwk dahlah capek", "Rp. 50.000.000.000,00", "Rp. 50.000.000.000,00"),
            DataHarian("19:00", "Minum", "Tunai", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("20:00", "Cihuy", "ShopeePay", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("20:01", "Beli komik", "Cash", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("20:10", "Makan", "ShopeePay", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("20:30", "Makan Siang dan Makan-Makan", "ShopeePay Selalu di Depan tapi di belakang juga ada sih wkwkwkwk dahlah capek", "Rp. 50.000.000.000,00", "Rp. 50.000.000.000,00")
        )

        val detailSelasa = listOf(
            DataHarian("08:00", "Sarapan", "Tunai", "Rp. 20.000,00", "Rp. 20.000,00"),
            DataHarian("12:30", "Bensin", "BRI", "Rp. 50.000,00", "Rp. 50.000,00"),
            DataHarian("19:45", "Nonton", "OVO", "Rp. 35.000,00", "Rp. 35.000,00")
        )

        val detailRabu = listOf(
            DataHarian("07:30", "Donat", "ShopeePay", "Rp. 10.000,00", "Rp. 10.000,00"),
            DataHarian("13:00", "Makan Siang", "Tunai", "Rp. 25.000,00", "Rp. 25.000,00")
        )

        val detailKamis = listOf(
            DataHarian("09:00", "Kopi", "DANA", "Rp. 15.000,00", "Rp. 15.000,00"),
            DataHarian("17:30", "Top Up Game", "OVO", "Rp. 100.000,00", "Rp. 100.000,00"),
            DataHarian("20:00", "Martabak", "Tunai", "Rp. 40.000,00", "Rp. 40.000,00")
        )

        val detailJumat = listOf(
            DataHarian("06:45", "Roti", "ShopeePay", "Rp. 12.000,00", "Rp. 12.000,00"),
            DataHarian("14:00", "Parkir", "Tunai", "Rp. 5.000,00", "Rp. 5.000,00")
        )

        val detailSabtu = listOf(
            DataHarian("10:00", "Laundry", "BRI", "Rp. 30.000,00", "Rp. 30.000,00"),
            DataHarian("16:30", "Minuman", "GoPay", "Rp. 18.000,00", "Rp. 18.000,00"),
            DataHarian("21:00", "Streaming", "OVO", "Rp. 25.000,00", "Rp. 25.000,00")
        )

        val detailMinggu = listOf(
            DataHarian("09:30", "Makan Pagi", "Tunai", "Rp. 22.000,00", "Rp. 22.000,00"),
            DataHarian("11:00", "Belanja Pasar", "DANA", "Rp. 75.000,00", "Rp. 75.000,00")
        )

        val dummySummaryData = listOf(
            RangkumanHarian("Mon", "12/05/2025", "Rp. 3.500.000.000,00", "Rp. 1.300.000.,00", detailSenin),
            RangkumanHarian("Tue", "13/05/2025", "Rp. 105.000,00", "Rp. 105.000,00", detailSelasa),
            RangkumanHarian("Wed", "14/05/2025", "Rp. 35.000,00", "Rp. 35.000,00", detailRabu),
            RangkumanHarian("Thu", "15/05/2025", "Rp. 155.000,00", "Rp. 155.000,00", detailKamis),
            RangkumanHarian("Fri", "16/05/2025", "Rp. 17.000,00", "Rp. 17.000,00", detailJumat),
            RangkumanHarian("Sat", "17/05/2025", "Rp. 73.000,00", "Rp. 73.000,00", detailSabtu),
            RangkumanHarian("Sun", "18/05/2025", "Rp. 97.000,00", "Rp. 97.000,00", detailMinggu)
        )

        adapter = HarianAdapter(dummySummaryData)
        recyclerView.adapter = adapter
    }
}