package com.fintrack.app.ui.profile

import com.fintrack.app.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.ui.signin.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
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

        firebaseAuth = FirebaseAuth.getInstance()

        // Inisialisasi GoogleSignInClient (wajib untuk logout dari Google juga)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Tampilkan informasi user
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
            tvUserName.text = user.displayName ?: "Tidak ada nama"
            tvUserEmail.text = user.email ?: "Tidak ada email"
        }

        btLogout.setOnClickListener {
            // Sign out dari Firebase dan Google
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                displayToast("Logout berhasil")

                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
