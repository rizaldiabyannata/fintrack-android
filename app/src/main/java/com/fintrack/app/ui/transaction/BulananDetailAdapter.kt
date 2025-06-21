package com.fintrack.app.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class BulananDetailAdapter(private val details: List<DataBulanan>) :
    RecyclerView.Adapter<BulananDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_detail_date)
        val tvIncome: TextView = itemView.findViewById(R.id.tv_detail_income)
        val tvExpense: TextView = itemView.findViewById(R.id.tv_detail_expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_month_detail, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detail = details[position]
        holder.tvDate.text = detail.tanggal
        holder.tvIncome.text = detail.pendapatan
        holder.tvExpense.text = detail.pengeluaran
    }

    override fun getItemCount(): Int = details.size
}