package com.fintrack.app.ui.statistik

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R
import com.fintrack.app.databinding.ItemListDataStatistikBinding
import java.text.NumberFormat
import java.util.*


class ItemDetailAdapter(private val detailItems: List<DetailItem>) :
    RecyclerView.Adapter<ItemDetailAdapter.DetailViewHolder>() {

    class DetailViewHolder(private val binding: ItemListDataStatistikBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvPercent: TextView = binding.tvPercent
        val tvLabel: TextView = binding.tvLabel
        val tvAmount: TextView = binding.tvAmount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemListDataStatistikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return detailItems.size
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = detailItems[position]

        val percent = (item.value.toFloat() / item.total.toFloat()) * 100
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        holder.tvPercent.text = String.format("%.0f%%", percent)
        holder.tvPercent.setBackgroundColor(item.color)
        holder.tvPercent.setTextColor(Color.WHITE)

        holder.tvLabel.text = item.label
        holder.tvAmount.text = format.format(item.value)
        holder.tvAmount.setTextColor(item.color)
    }
}
