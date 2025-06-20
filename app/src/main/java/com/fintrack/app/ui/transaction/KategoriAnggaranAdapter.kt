package com.fintrack.app.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class KategoriAnggaranAdapter(private val categoryList: List<BudgetCategory>) :
    RecyclerView.Adapter<KategoriAnggaranAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.image_view_kategori_icon)
        val nameTextView: TextView = itemView.findViewById(R.id.text_view_nama_kategori)
        val amountTextView: TextView = itemView.findViewById(R.id.text_view_jumlah_anggaran)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kategori_anggaran, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryList[position]
        holder.iconImageView.setImageResource(currentItem.iconResId)
        holder.nameTextView.text = currentItem.name
        holder.amountTextView.text = "Rp. ${String.format("%,.0f", currentItem.amount).replace(",", ".")},00"
    }

    override fun getItemCount() = categoryList.size
}