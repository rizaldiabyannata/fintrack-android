package com.fintrack.app.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class DaftarAnggaranAdapter(private val budgetList: List<BudgetItem>) :
    RecyclerView.Adapter<DaftarAnggaranAdapter.BudgetItemViewHolder>() {

    // Listener untuk menangani klik tombol edit
    var onEditClickListener: ((BudgetItem) -> Unit)? = null

    class BudgetItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.image_view_anggaran_icon)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.text_view_nama_kategori_anggaran)
        val budgetedAmountTextView: TextView = itemView.findViewById(R.id.text_view_jumlah_anggaran_item)
        val editButton: ImageView = itemView.findViewById(R.id.button_edit_anggaran_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daftar_anggaran, parent, false)
        return BudgetItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BudgetItemViewHolder, position: Int) {
        val currentItem = budgetList[position]
        holder.iconImageView.setImageResource(currentItem.iconResId)
        holder.categoryNameTextView.text = currentItem.categoryName
        holder.budgetedAmountTextView.text = "Rp. ${String.format("%,.0f", currentItem.budgetedAmount).replace(",", ".")},00"

        holder.editButton.setOnClickListener {
            onEditClickListener?.invoke(currentItem)
        }
    }

    override fun getItemCount() = budgetList.size
}