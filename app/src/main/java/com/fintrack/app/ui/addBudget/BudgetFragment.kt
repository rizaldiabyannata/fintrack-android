package com.fintrack.app.ui.addBudget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController // <-- IMPORT INI
import androidx.recyclerview.widget.LinearLayoutManager
import com.fintrack.app.R
import com.fintrack.app.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ... kode untuk setup RecyclerView ...
        val dataBudget = listOf(
            BudgetItem("Belanja Bulanan", 1500000, 10.000,R.drawable.ic_people),
            BudgetItem("Transportasi", 300000,  10.000,R.drawable.ic_people),
            BudgetItem("Cicilan Rumah", 2500000, 10.000,R.drawable.ic_people),
            BudgetItem("Dana Darurat", 500000, 10.000,R.drawable.ic_people),
            BudgetItem("Hiburan", 450000, 10.000,R.drawable.ic_people)
        )

        val adapter = BudgetAdapter(dataBudget)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        binding.backButtonBudget.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.addBudget.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddBudgetFragment())
                .addToBackStack(null) // Menambahkan transaksi ini ke back stack
                .commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}