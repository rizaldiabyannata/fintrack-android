package com.fintrack.app.ui.manageBudget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fintrack.app.R
import com.fintrack.app.data.BudgetRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.databinding.FragmentManageBudgetBinding
import com.fintrack.app.ui.addBudget.AddBudgetFragment
import com.fintrack.app.utils.CategoryIconMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ManageBudgetFragment : Fragment() {

    private var _binding: FragmentManageBudgetBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var budgetRepository: BudgetRepository

    private lateinit var manageBudgetAdapter: ManageBudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        fetchDataDirectly()
    }

    private fun setupRecyclerView() {
        manageBudgetAdapter = ManageBudgetAdapter(emptyList())
        binding.recyclerView.apply {
            adapter = manageBudgetAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.backButtonBudget.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.addBudget.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddBudgetFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchDataDirectly() {
        viewLifecycleOwner.lifecycleScope.launch {
            budgetRepository.getAllBudgets().collectLatest { apiResponse ->
                binding.recyclerView.isVisible = apiResponse is ApiResponse.Success

                when(apiResponse) {
                    is ApiResponse.Success -> {
                        val uiItems = apiResponse.data.mapNotNull { networkItem ->
                            networkItem?.let {
                                val categoryName = it.category?.name ?: "Tanpa Kategori"
                                BudgetItem(
                                    name = categoryName,
                                    amount = it.amountLimit ?: 0,
                                    used = 0.0, // Anda bisa sesuaikan ini jika API mengembalikan nilai terpakai
                                    // Menggunakan CategoryIconMapper yang terpusat
                                    iconResId = CategoryIconMapper.getIconForCategory(categoryName)
                                )
                            }
                        }
                        manageBudgetAdapter.updateData(uiItems)
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, apiResponse.errorMessage, Toast.LENGTH_LONG).show()
                    }
                    is ApiResponse.Loading -> {
                        // Handle loading state jika Anda memiliki progress bar
                    }
                }
            }
        }
    }

    // Fungsi lokal mapCategoryToIcon telah dihapus karena sudah digantikan oleh CategoryIconMapper

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
