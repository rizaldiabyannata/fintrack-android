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

class HarianAdapter(
    private val dataList: List<RangkumanHarian>
) : RecyclerView.Adapter<HarianAdapter.HarianViewHolder>() {

    inner class HarianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHarian: TextView = itemView.findViewById(R.id.tv_day)
        val txtTanggal: TextView = itemView.findViewById(R.id.tv_date)
        val txtPendapatan: TextView = itemView.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarianViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_harian, parent, false)
        return HarianViewHolder(view)
    }

    override fun onBindViewHolder(holder: HarianViewHolder, position: Int) {
        val item = dataList[position]

        holder.txtHarian.text = item.hari
        holder.txtTanggal.text = item.tanggal
        holder.txtPendapatan.text = item.totalPendapatan

        // Clear previous rows except header (index 0)


        // Tambahkan baris data detail (kecuali baris header)
        for (data in item.detail) {
            val row = TableRow(holder.itemView.context)

            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            row.addView(createScrollableTextView(holder.itemView.context, data.waktu))
            row.addView(createScrollableTextView(holder.itemView.context, data.jenis))
            row.addView(createScrollableTextView(holder.itemView.context, data.media))
            row.addView(createScrollableTextView(holder.itemView.context, data.pendapatan))
            row.addView(createScrollableTextView(holder.itemView.context, data.pengeluaran))

        }

        // Opsional: bisa tambahkan fungsi expand/collapse TableLayout
        // Contoh: klik kartu -> tampilkan atau sembunyikan tabel

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
