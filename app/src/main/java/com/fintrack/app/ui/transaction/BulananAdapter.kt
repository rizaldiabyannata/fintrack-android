package com.fintrack.app.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class BulananAdapter(
    private var dataList: List<RangkumanBulanan>
) : RecyclerView.Adapter<BulananAdapter.BulananViewHolder>() {

    inner class BulananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referensi ke view di header
        val headerContainer: ConstraintLayout = itemView.findViewById(R.id.header_container)
        val txtBulan: TextView = itemView.findViewById(R.id.tv_month)
        val txtTahun: TextView = itemView.findViewById(R.id.tv_year)
        val txtPendapatan: TextView = itemView.findViewById(R.id.tv_income)
        val txtPengeluaran: TextView = itemView.findViewById(R.id.tv_expense)

        // Referensi ke container dan RecyclerView untuk detail
        val expandableContainer: LinearLayout = itemView.findViewById(R.id.expandable_container)
        val rvDetails: RecyclerView = itemView.findViewById(R.id.rv_details) // Menggunakan rv_details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_bulanan, parent, false)
        return BulananViewHolder(view)
    }

    override fun onBindViewHolder(holder: BulananViewHolder, position: Int) {
        val item = dataList[position]

        // 1. Set data utama pada kartu header
        holder.txtBulan.text = item.bulan
        holder.txtTahun.text = item.tahun
        holder.txtPendapatan.text = item.totalPendapatan
        holder.txtPengeluaran.text = item.totalPengeluaran

        // 2. LOGIKA BARU: Setup inner RecyclerView
        holder.rvDetails.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvDetails.adapter = BulananDetailAdapter(item.detail) // Gunakan adapter detail yang baru

        // 3. Atur visibilitas awal (selalu tertutup saat di-bind ulang)
        holder.expandableContainer.visibility = View.GONE

        // 4. Atur OnClickListener untuk expand/collapse
        holder.headerContainer.setOnClickListener {
            val isVisible = holder.expandableContainer.visibility == View.VISIBLE
            holder.expandableContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<RangkumanBulanan>) {
        this.dataList = newData
        notifyDataSetChanged()
    }
}