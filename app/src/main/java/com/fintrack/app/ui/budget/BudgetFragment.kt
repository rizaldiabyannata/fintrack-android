package com.fintrack.app.ui.budget

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
import com.fintrack.app.data.network.ApiResponse // Pastikan Anda mengimpor kelas ApiResponse
import com.fintrack.app.data.BudgetRepository // Pastikan Anda mengimpor BudgetRepository
import com.fintrack.app.data.response.GetAllBudgetResponse // Pastikan import ini benar
import com.fintrack.app.databinding.FragmentBudgetBinding
import com.fintrack.app.ui.addBudget.AddBudgetFragment
import com.fintrack.app.ui.budget.BudgetItem
import com.fintrack.app.ui.budget.BudgetAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint // Anotasi wajib untuk Hilt
class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    // Menyuntikkan Repository langsung ke Fragment.
    @Inject
    lateinit var budgetRepository: BudgetRepository

    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        fetchDataDirectly() // Memanggil data langsung dari repository
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(emptyList())
        binding.recyclerView.apply {
            adapter = budgetAdapter
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
            budgetRepository.getAllBudget().collectLatest { apiResponse ->
                binding.recyclerView.isVisible = apiResponse is ApiResponse.Success

                when(apiResponse) {
                    is ApiResponse.Success -> {
                        // The 'data' property is now the list itself
                        val uiItems = apiResponse.data.mapNotNull { networkItem ->
                            networkItem?.let {
                                BudgetItem(
                                    // Access the 'name' from the nested category object
                                    name = it.category?.name ?: "Tanpa Kategori",
                                    amount = it.amountLimit ?: 0,
                                    used = 0.0,
                                    // Pass the category name to the icon mapper
                                    iconResId = mapCategoryToIcon(it.category?.name)
                                )
                            }
                        }
                        budgetAdapter.updateData(uiItems)
                    }
                    is ApiResponse.Error -> {
                        Toast.makeText(context, apiResponse.errorMessage, Toast.LENGTH_LONG).show()
                    }
                    is ApiResponse.Loading -> {
                        // Handle loading state if you have a progress bar
                    }
                }
            }
        }
    }

    // Update the function signature to accept the category name
    private fun mapCategoryToIcon(categoryName: String?): Int {
        return when (categoryName?.lowercase()) {
            "makanan", "belanja", "belanja bulanan", "ngentot" -> R.drawable.ic_people // Added new category
            "transportasi" -> R.drawable.ic_people
            "cicilan rumah", "tagihan" -> R.drawable.ic_people
            "hiburan" -> R.drawable.ic_people
            "dana darurat" -> R.drawable.ic_people
            else -> R.drawable.ic_people
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}