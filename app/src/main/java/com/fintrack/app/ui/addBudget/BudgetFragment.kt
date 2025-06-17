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
            BudgetItem("Belanja Bulanan", "Rp 1.500.000",R.drawable.ic_people),
            BudgetItem("Transportasi", "Rp 300.000",R.drawable.ic_people),
            BudgetItem("Cicilan Rumah", "Rp 2.500.000",R.drawable.ic_people),
            BudgetItem("Dana Darurat", "Rp 500.000",R.drawable.ic_people),
            BudgetItem("Hiburan", "Rp 450.000",R.drawable.ic_people)
        )

        val adapter = BudgetAdapter(dataBudget)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        binding.backButtonBudget.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // --- UBAH BAGIAN INI ---
        binding.addBudget.setOnClickListener {
            // Menggunakan NavController untuk berpindah ke AddBudgetFragment
            // Pastikan ID action ini sama dengan yang ada di file navigation XML
            findNavController().navigate(R.id.action_budgetFragment_to_addBudgetFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}