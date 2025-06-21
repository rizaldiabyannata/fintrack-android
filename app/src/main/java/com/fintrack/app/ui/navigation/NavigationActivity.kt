package com.fintrack.app.ui.navigation

import com.fintrack.app.ui.LainnyaFragment
import com.fintrack.app.ui.statistik.StatistikFragment
//import com.fintrack.app.ui.transaction.TransaksiFragment
import com.fintrack.app.ui.budget.BudgetFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.ui.transaction.TransaksiFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BudgetFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_transaksi -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BudgetFragment())
                        .commit()
                    true
                }
                R.id.nav_statistik -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, TransaksiFragment())
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