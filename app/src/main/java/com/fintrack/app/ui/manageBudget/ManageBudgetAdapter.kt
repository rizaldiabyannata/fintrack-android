package com.fintrack.app.ui.manageBudget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R
import java.text.NumberFormat
import java.util.Locale

// DIUBAH: Menambahkan listener pada constructor
class ManageBudgetAdapter(
    private var budgetList: List<BudgetItem>,
    private val onEditClick: (String) -> Unit
) :
    RecyclerView.Adapter<ManageBudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val currentItem = budgetList[position]

        holder.iconBudget.setImageResource(currentItem.iconResId)
        holder.textBudget.text = currentItem.name

        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        holder.totalBudget.text = formatter.format(currentItem.amount.toLong())

        // DIUBAH: Menambahkan aksi klik yang memanggil listener dengan ID
        holder.editIconBudget.setOnClickListener {
            onEditClick(currentItem.id)
        }
    }

    override fun getItemCount(): Int {
        return budgetList.size
    }

    fun updateData(newBudgetList: List<BudgetItem>) {
        budgetList = newBudgetList
        notifyDataSetChanged()
    }
}
