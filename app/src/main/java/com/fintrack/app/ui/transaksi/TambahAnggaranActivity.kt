package com.fintrack.app.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R

class TambahAnggaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_tambah_anggaran)

        val btnKembali = findViewById<ImageView>(R.id.button_kembali_anggaran)
        btnKembali.setOnClickListener {
            finish()
        }

        val btnTambah = findViewById<ImageView>(R.id.button_tambah_anggaran)
        btnTambah.setOnClickListener {
            val intent = Intent(this, TambahAnggaranBaruActivity::class.java)
            startActivity(intent)
        }

    }
}
