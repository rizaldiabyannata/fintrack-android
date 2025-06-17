package com.fintrack.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Anda dapat menambahkan logika di sini untuk memeriksa status login pengguna.
        // Jika pengguna belum login, arahkan ke SignInActivity.
        // Contoh:
        //
        // val userIsLoggedIn = false // Ganti dengan logika pengecekan login Anda
        // if (!userIsLoggedIn) {
        //     startActivity(Intent(this, SignInActivity::class.java))
        //     finish()
        // }
    }
}