package com.fintrack.app.ui

import LainnyaFragment
import StatistikFragment
import TransaksiFragment
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.databinding.ActivitySignInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class CihuyActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cihuy)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TransaksiFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_transaksi -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, TransaksiFragment())
                        .commit()
                    true
                }
                R.id.nav_statistik -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, StatistikFragment())
                        .commit()
                    true
                }
                R.id.nav_lainnya -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LainnyaFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
