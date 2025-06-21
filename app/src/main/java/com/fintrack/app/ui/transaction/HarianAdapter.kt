package com.fintrack.app.ui.transaction

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class HarianAdapter(
    private var dataList: List<RangkumanHarian>
) : RecyclerView.Adapter<HarianAdapter.HarianViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    // 1. Tambahkan header_container ke ViewHolder
    class HarianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hari: TextView = itemView.findViewById(R.id.tv_day)
        val tanggal: TextView = itemView.findViewById(R.id.tv_date)
        val totalPendapatan: TextView = itemView.findViewById(R.id.tv_income)
        val totalPengeluaran: TextView = itemView.findViewById(R.id.tv_expense)
        val detailLayout: LinearLayout = itemView.findViewById(R.id.expandable_container)
        val headerContainer: ConstraintLayout = itemView.findViewById(R.id.header_container) // Tambahkan ini
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarianViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_harian, parent, false)
        return HarianViewHolder(view)
    }

    override fun onBindViewHolder(holder: HarianViewHolder, position: Int) {
        val item = dataList[position]
        holder.hari.text = item.hari
        holder.tanggal.text = item.tanggal
        holder.totalPendapatan.text = item.totalPendapatan
        holder.totalPengeluaran.text = item.totalPengeluaran

        val isExpanded = expandedPositions.contains(position)
        holder.detailLayout.isVisible = isExpanded

        if (isExpanded) {
            holder.detailLayout.removeAllViews()
            if (item.detail.isNotEmpty()) {
                item.detail.forEach { detail ->
                    val detailView = createDetailItemView(holder.itemView.context, detail, holder.detailLayout)
                    holder.detailLayout.addView(detailView)
                }
            }
        }

        // 2. Pindahkan OnClickListener ke header_container
        holder.headerContainer.setOnClickListener {
            Log.d("HarianAdapterClick", "Header at position $position clicked! isExpanded was $isExpanded")

            if (isExpanded) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    private fun createDetailItemView(context: Context, detail: DataHarian, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val detailView = inflater.inflate(R.layout.item_transaction_detail, parent, false)

        val timeTv: TextView = detailView.findViewById(R.id.tv_time)
        val descriptionTv: TextView = detailView.findViewById(R.id.tv_description)
        val amountTv: TextView = detailView.findViewById(R.id.tv_amount)

        timeTv.text = detail.waktu
        descriptionTv.text = "${detail.jenis} (${detail.media})"

        if (detail.pendapatan != "Rp. 0") {
            amountTv.text = detail.pendapatan
            amountTv.setTextColor(ContextCompat.getColor(context, R.color.primaryBlue))
        } else {
            amountTv.text = detail.pengeluaran
            amountTv.setTextColor(ContextCompat.getColor(context, R.color.primaryRed))
        }

        return detailView
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newDataList: List<RangkumanHarian>) {
        this.dataList = newDataList
        expandedPositions.clear()
        notifyDataSetChanged()
    }
}
