package com.fintrack.app.ui.closeBook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.databinding.ItemCategoryBinding
import com.fintrack.app.ui.manageBudget.BudgetItem
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter untuk RecyclerView kategori yang datanya bisa diperbarui.
 */
// Mengubah konstruktor untuk menerima list yang bisa diubah
class BudgetAdapter(private var budgetItems: MutableList<BudgetItem> = mutableListOf()) :
    RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    /**
     * ViewHolder memegang view untuk setiap item dalam daftar.
     */
    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(budgetItem: BudgetItem) {
            binding.imageCategoryIcon.setImageResource(budgetItem.iconResId)
            binding.textCategoryName.text = budgetItem.name

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            val formattedAmount = formatRupiah.format(budgetItem.amount.toLong())
                .replace("Rp", "Rp. ")
                .replace(",00", "")
            binding.textCategoryAmount.text = formattedAmount
        }
    }

    /**
     * Fungsi baru untuk memperbarui data di adapter.
     * @param newItems Daftar BudgetItem baru yang akan ditampilkan.
     */
    fun updateData(newItems: List<BudgetItem>) {
        budgetItems.clear()
        budgetItems.addAll(newItems)
        notifyDataSetChanged() // Memberi tahu RecyclerView bahwa data telah berubah
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
