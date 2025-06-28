package com.fintrack.app.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.data.SessionManager
import com.fintrack.app.ui.profile.ProfileFragment
import com.fintrack.app.ui.signin.SignInActivity
import com.fintrack.app.ui.statistik.StatistikFragment
import com.fintrack.app.ui.transaction.TransaksiFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    // Suntikkan SessionManager untuk memeriksa status login
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.isLoggedIn()) {
            Log.d("NavigationActivity", "User not logged in. Navigating to SignInActivity.")
            navigateToSignIn()
            return
        }

        // Jika sudah login, lanjutkan proses setup seperti biasa
        setContentView(R.layout.activity_navigation)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment saat activity pertama kali dibuat
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TransaksiFragment())
                .commit()
        }

        // Setup listener untuk bottom navigation
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.nav_transaksi -> TransaksiFragment()
                R.id.nav_statistik -> StatistikFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> null
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
                true
            } else {
                false
            }
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Tutup NavigationActivity
    }
}
