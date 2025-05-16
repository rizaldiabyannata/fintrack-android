package com.fintrack.app.ui.transaksi

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fintrack.app.R

class BulananAdapter(
    private val dataList: List<DataBulanan>
) : RecyclerView.Adapter<BulananAdapter.BulananViewHolder>() {

    inner class BulananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBulan: TextView = itemView.findViewById(R.id.txt_bulan)
        val txtTanggal: TextView = itemView.findViewById(R.id.txt_tanggalBulanan)
        val txtPendapatan: TextView = itemView.findViewById(R.id.txt_pendapatanBulanan)
        val txtPengeluaran: TextView = itemView.findViewById(R.id.txt_pengeluaranBulanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi_bulanan, parent, false)
        return BulananViewHolder(view)
    }

    override fun onBindViewHolder(holder: BulananViewHolder, position: Int) {
        val item = dataList[position]

        holder.txtBulan.text = item.bulan
        holder.txtTanggal.text = item.tanggal
        holder.txtPendapatan.text = item.pendapatan
        holder.txtPengeluaran.text = item.pengeluaran

//        holder.txtJenisTransaksi.setOnClickListener {
//            AlertDialog.Builder(holder.itemView.context)
//                .setTitle("Jenis Transaksi")
//                .setMessage(item.jenisTransaksi)
//                .setPositiveButton("OK", null)
//                .show()
//        }
//
//        holder.txtMediaTransaksi.setOnClickListener {
//            AlertDialog.Builder(holder.itemView.context)
//                .setTitle("Media Transaksi")
//                .setMessage(item.mediaTransaksi)
//                .setPositiveButton("OK", null)
//                .show()
//        }

    }

    override fun getItemCount(): Int = dataList.size
}
