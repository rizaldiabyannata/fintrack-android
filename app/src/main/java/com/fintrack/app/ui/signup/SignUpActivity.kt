package com.fintrack.app.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.AuthRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.RegisterRequest
import com.fintrack.app.data.request.UserPayload
import com.fintrack.app.databinding.ActivitySignUpBinding
import com.fintrack.app.ui.otp.OTPVerificationActivity
import com.fintrack.app.ui.signin.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint // 1. Aktifkan Hilt untuk Activity ini
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    // 2. Inject dependensi yang dibutuhkan menggunakan Hilt
    @Inject lateinit var repository: AuthRepository
    @Inject lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val TAG = "SignUpActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupClickListeners() {
        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmailPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(name, email, password)) {
                val request = RegisterRequest(name, email, password)
                signUpWithEmailPassword(request)
            }
        }

        binding.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    /**
     * Memulai proses registrasi dengan memanggil repository.
     * Menggunakan lifecycleScope untuk menjalankan coroutine di dalam Activity.
     */
    private fun signUpWithEmailPassword(request: RegisterRequest) {
        // 3. Gunakan lifecycleScope untuk memanggil fungsi dari repository
        lifecycleScope.launch {
            repository.register(request).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        Toast.makeText(this@SignUpActivity, state.data.message, Toast.LENGTH_LONG).show()
                        // Arahkan ke layar OTP setelah registrasi berhasil
                        val intent = Intent(this@SignUpActivity, OTPVerificationActivity::class.java).apply {
                            putExtra(OTPVerificationActivity.EXTRA_EMAIL, request.email)
                            putExtra(OTPVerificationActivity.EXTRA_OTP_TYPE, OTPVerificationActivity.OTPType.EMAIL_VERIFICATION.value)
                        }
                        startActivity(intent)
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        Toast.makeText(this@SignUpActivity, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                    else -> showLoading(false) // Handle Empty state
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                showLoading(false)
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoading(true) // Tampilkan loading sebelum proses Firebase
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseToken = tokenTask.result?.token
                        if (firebaseToken != null && user != null) {
                            val payload = UserPayload(user.uid, user.displayName ?: "", user.email ?: "", "google")
                            // Panggil fungsi yang memanggil repository untuk Google login
                            loginWithGoogle(firebaseToken, payload)
                        } else {
                            showLoading(false)
                        }
                    } else {
                        showLoading(false)
                        Toast.makeText(this, "Failed to get auth token.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                showLoading(false)
                Toast.makeText(this, "Firebase authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithGoogle(token: String, payload: UserPayload) {
        lifecycleScope.launch {
            repository.loginWithGoogle(token, payload).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true) // Sebenarnya loading sudah jalan
                    is ApiResponse.Success -> {
                        showLoading(false)
                        navigateToSignIn()
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        Toast.makeText(this@SignUpActivity, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            binding.etName.requestFocus()
            return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailPhone.error = "Enter a valid email"
            binding.etEmailPhone.requestFocus()
            return false
        }
        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        // Anda mungkin perlu menambahkan ProgressBar di file XML Anda
        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnCreateAccount.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
