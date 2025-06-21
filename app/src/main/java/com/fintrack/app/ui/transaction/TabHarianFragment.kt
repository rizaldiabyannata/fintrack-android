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

        recyclerView = view.findViewById(R.id.recyclerViewHarian)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = HarianAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    fun submitHarianData(data: List<RangkumanHarian>) {
        if (::adapter.isInitialized) {
            adapter.updateData(data)
        }
    }
}
