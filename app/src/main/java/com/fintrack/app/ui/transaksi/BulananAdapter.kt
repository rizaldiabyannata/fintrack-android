package com.fintrack.app.ui.transaksi

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class BulananAdapter(
    private val dataList: List<RangkumanBulanan>
) : RecyclerView.Adapter<BulananAdapter.BulananViewHolder>() {

    inner class BulananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBulan: TextView = itemView.findViewById(R.id.txt_bulan)
        val txtTanggal: TextView = itemView.findViewById(R.id.txt_tahunBulanan)
        val txtPendapatan: TextView = itemView.findViewById(R.id.txt_pendapatanBulanan)
        val txtPengeluaran: TextView = itemView.findViewById(R.id.txt_pengeluaranBulanan)
        val tableDetail: TableLayout = itemView.findViewById(R.id.table_detail_bulanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_bulanan, parent, false)
        return BulananViewHolder(view)
    }

    override fun onBindViewHolder(holder: BulananViewHolder, position: Int) {
        val item = dataList[position]

        holder.txtBulan.text = item.bulan
        holder.txtTanggal.text = item.tahun
        holder.txtPendapatan.text = item.totalPendapatan
        holder.txtPengeluaran.text = item.totalPengeluaran

        // Clear previous rows except header (index 0)
        val childCount = holder.tableDetail.childCount
        if (childCount > 1) {
            holder.tableDetail.removeViews(1, childCount - 1)
        }

        // Tambahkan baris data detail (kecuali baris header)
        for (data in item.detail) {
            val row = TableRow(holder.itemView.context)

            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            row.addView(createScrollableTextView(holder.itemView.context, data.tanggal))
            row.addView(createScrollableTextView(holder.itemView.context, data.pendapatan))
            row.addView(createScrollableTextView(holder.itemView.context, data.pengeluaran))

            holder.tableDetail.addView(row)
        }

        // Opsional: bisa tambahkan fungsi expand/collapse TableLayout
        // Contoh: klik kartu -> tampilkan atau sembunyikan tabel
        holder.itemView.setOnClickListener {
            holder.tableDetail.visibility = if (holder.tableDetail.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    private fun createScrollableTextView(context: Context, text: String): HorizontalScrollView {
        val textView = TextView(context).apply {
            layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            this.text = text
            textSize = 12f
            setPadding(6, 6, 6, 6)
            isSingleLine = true
            setHorizontallyScrolling(true) // ini pengganti isHorizontallyScrolling = true
            ellipsize = null
        }

        return HorizontalScrollView(context).apply {
            layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            isHorizontalScrollBarEnabled = false
            addView(textView)
        }
    }
}

