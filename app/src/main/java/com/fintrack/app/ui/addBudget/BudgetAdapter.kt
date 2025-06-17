package com.fintrack.app.ui.addBudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class BudgetAdapter(private val anggaranList: List<BudgetItem>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Tambahkan referensi untuk iconBudget
        val iconBudget: ImageView = itemView.findViewById(R.id.iconBudget)
        val textBudget: TextView = itemView.findViewById(R.id.textBudget)
        val totalBudget: TextView = itemView.findViewById(R.id.totalBudget)
        val editIconBudget: ImageView = itemView.findViewById(R.id.editIconBudget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val currentItem = anggaranList[position]

        // Atur gambar pada ImageView menggunakan ID resource (Int)
        holder.iconBudget.setImageResource(currentItem.iconBudget)
        holder.textBudget.text = currentItem.nama
        holder.totalBudget.text = currentItem.total

        holder.editIconBudget.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Edit item: ${currentItem.nama}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return anggaranList.size
    }
}