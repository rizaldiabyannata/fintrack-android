package com.fintrack.app.ui.resetPassword

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.data.AuthRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.databinding.ActivityResetPasswordBinding
import com.fintrack.app.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var repository: AuthRepository

    private var userEmail: String = ""

    companion object {
        private const val TAG = "ResetPasswordActivity"
        const val PREFS_NAME = "user_prefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        getDataFromIntent()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.submitButton.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun getDataFromIntent() {
        userEmail = intent.getStringExtra("email") ?: ""
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Sesi tidak valid. Silakan ulangi proses lupa password.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun validateAndSubmit() {
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (validatePasswords(newPassword, confirmPassword)) {
            setNewPassword(newPassword)
        }
    }

    private fun validatePasswords(newPassword: String, confirmPassword: String): Boolean {
        binding.tvResetPasswordInput.error = null
        binding.tvResetPasswordConfirmation.error = null
        var isValid = true

        if (newPassword.length < 6) {
            binding.tvResetPasswordInput.error = "Password minimal 6 karakter"
            isValid = false
        }

        if (newPassword != confirmPassword) {
            binding.tvResetPasswordConfirmation.error = "Password tidak cocok"
            isValid = false
        }
        return isValid
    }

    private fun setNewPassword(newPassword: String) {
        lifecycleScope.launch {
            repository.setNewPassword(userEmail, newPassword).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        Toast.makeText(this@ResetPasswordActivity, state.data.message, Toast.LENGTH_LONG).show()
                        // Tunda navigasi agar user bisa membaca Toast
                        Handler(Looper.getMainLooper()).postDelayed({
                            navigateToLogin()
                        }, 2000)
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        Toast.makeText(this@ResetPasswordActivity, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Ganti dengan ProgressBar jika ada di XML Anda
        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.submitButton.isEnabled = !isLoading
        binding.etNewPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
