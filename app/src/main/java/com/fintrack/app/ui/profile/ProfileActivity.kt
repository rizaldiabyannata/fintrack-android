package com.fintrack.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.data.SessionManager
import com.fintrack.app.ui.signin.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var sessionManager: SessionManager // Inject SessionManager

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        btLogout = findViewById(R.id.bt_logout)

        setupGoogleSignInClient()
        loadUserProfile()

        btLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupGoogleSignInClient(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun loadUserProfile() {
        // Ambil data dari SessionManager
        val name = sessionManager.getUserName() ?: "Tidak ada nama"
        val email = sessionManager.getUserEmail() ?: "Tidak ada email"

        tvUserName.text = name
        tvUserEmail.text = email
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            // Gunakan SessionManager untuk membersihkan sesi
            sessionManager.clearSession()

            displayToast("Logout berhasil")
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
