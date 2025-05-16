package com.fintrack.app.ui.transaksi

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class HarianAdapter(
    private val dataList: List<DataHarian>
) : RecyclerView.Adapter<HarianAdapter.HarianViewHolder>() {

    inner class HarianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHari: TextView = itemView.findViewById(R.id.txt_hari)
        val txtTanggal: TextView = itemView.findViewById(R.id.txt_tanggalHarian)
        val txtPendapatan: TextView = itemView.findViewById(R.id.txt_pendapatanHarian)
        val txtJenisTransaksi: TextView = itemView.findViewById(R.id.txt_jenis_transaksi)
        val txtMediaTransaksi: TextView = itemView.findViewById(R.id.txt_media_transaksi)
        val txtPengeluaran: TextView = itemView.findViewById(R.id.txt_pengeluaranHarian)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarianViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_harian, parent, false)
        return HarianViewHolder(view)
    }

    override fun onBindViewHolder(holder: HarianViewHolder, position: Int) {
        val item = dataList[position]

        holder.txtHari.text = item.hari
        holder.txtTanggal.text = item.tanggal
        holder.txtPendapatan.text = item.pendapatan
        holder.txtJenisTransaksi.text = item.jenisTransaksi
        holder.txtMediaTransaksi.text = item.mediaTransaksi
        holder.txtPengeluaran.text = item.pengeluaran

        holder.txtJenisTransaksi.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Jenis Transaksi")
                .setMessage(item.jenisTransaksi)
                .setPositiveButton("OK", null)
                .show()
        }

        holder.txtMediaTransaksi.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Media Transaksi")
                .setMessage(item.mediaTransaksi)
                .setPositiveButton("OK", null)
                .show()
        }

    }

    override fun getItemCount(): Int = dataList.size
}
