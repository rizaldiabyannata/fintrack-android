package com.fintrack.app.ui.closeBook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R
import com.fintrack.app.databinding.ItemCategoryBinding
import com.fintrack.app.ui.manageBudget.BudgetItem
import java.text.NumberFormat
import java.util.Locale

class BudgetAdapter(private var budgetItems: MutableList<BudgetItem> = mutableListOf()) :
    RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(budgetItem: BudgetItem) {
            binding.imageCategoryIcon.setImageResource(budgetItem.iconResId)
            binding.textCategoryName.text = budgetItem.name

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            val formattedAmount = formatRupiah.format(budgetItem.amount.toLong())
                .replace("Rp", "Rp ")
                .replace(",00", "")
            binding.textCategoryAmount.text = formattedAmount

            // DILAKUKAN PERBAIKAN: Kalkulasi persentase penggunaan dilakukan di sini
            val usagePercentage = if (budgetItem.amount > 0) {
                ((budgetItem.used / budgetItem.amount) * 100).toInt()
            } else {
                0
            }

            // Terapkan nilai yang sudah dihitung ke UI
            binding.progressCategory.progress = usagePercentage
            binding.textCategoryPercentage.text = "$usagePercentage% terpakai"

        }
    }

    fun updateData(newItems: List<BudgetItem>) {
        budgetItems.clear()
        budgetItems.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(budgetItems[position])
    }

    override fun getItemCount(): Int = budgetItems.size
}
