package com.fintrack.app.ui.transaksi

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R

class TambahAnggaranBaruActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_anggaran_baru)

        val btnKembali = findViewById<ImageView>(R.id.button_kembali_tambah_anggaran)
        btnKembali.setOnClickListener {
            finish()
        }

        val buttonSimpan = findViewById<Button>(R.id.button_simpan_anggaran)
        buttonSimpan.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Berhasil")
                .setMessage("Anggaran berhasil disimpan.")
                .setPositiveButton("Oke") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setCancelable(false)
                .show()
        }
    }
}
