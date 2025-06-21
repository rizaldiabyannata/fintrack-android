package com.fintrack.app.ui.transaction

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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Mulai dengan list kosong
        adapter = BulananAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    fun submitBulananData(data: List<RangkumanBulanan>) {
        if (::adapter.isInitialized) {
            adapter.updateData(data)
        }
    }
}