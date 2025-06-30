package com.fintrack.app.ui.otp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.data.AuthRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.databinding.ActivityOtpBinding
import com.fintrack.app.ui.resetPassword.ResetPasswordActivity
import com.fintrack.app.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private lateinit var otpEditTexts: List<EditText>

    @Inject
    lateinit var repository: AuthRepository

    private lateinit var userEmail: String
    private lateinit var otpType: OTPType

    // --- PERBAIKAN 1: Membuat Enum menjadi Serializable ---
    // Ini memungkinkan objek enum untuk dikirim melalui Intent dengan aman.
    enum class OTPType(val value: String) : java.io.Serializable {
        EMAIL_VERIFICATION("verification"),
        RESET_PASSWORD("password")
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_OTP_TYPE = "extra_otp_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra(EXTRA_EMAIL) ?: ""

        // --- PERBAIKAN 2: Mengambil data enum dari Intent dengan cara yang benar ---
        // Menggunakan getSerializableExtra untuk mengambil objek enum secara langsung.
        otpType = intent.getSerializableExtra(EXTRA_OTP_TYPE) as? OTPType ?: OTPType.EMAIL_VERIFICATION

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Sesi tidak valid.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        setupOTPInput()
        setupClickListeners()
        updateUITexts()
    }

    private fun initializeViews() {
        otpEditTexts = with(binding) {
            listOf(otp1, otp2, otp3, otp4, otp5)
        }
    }

    private fun updateUITexts() {
        val maskedEmail = userEmail.replaceRange(2, userEmail.indexOf('@'), "*****")
        binding.tvOtpDescription.text = "We've sent a 5-digit verification code to $maskedEmail"
        if(otpType == OTPType.RESET_PASSWORD) {
            binding.tvEnterOtp.text = "Reset Password"
        }
    }

    private fun setupClickListeners() {
        binding.btnSubmitOtp.setOnClickListener {
            val otp = otpEditTexts.joinToString("") { it.text.toString() }
            if (otp.length == 5) {
                submitOTP(otp)
            } else {
                Toast.makeText(this, "Please enter the complete OTP.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvResendOtp.setOnClickListener {
            resendOtp()
        }
    }

    // Fungsi submitOTP sudah benar dan tidak perlu diubah,
    // karena sekarang 'otpType' akan memiliki nilai yang tepat.
    private fun submitOTP(otp: String) {
        lifecycleScope.launch {
            val flow = when (otpType) {
                OTPType.EMAIL_VERIFICATION -> repository.verifyEmailOtp(userEmail, otp)
                OTPType.RESET_PASSWORD -> repository.verifyResetPasswordOtp(userEmail, otp)
            }

            flow.collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        // Penanganan berdasarkan jenis OTP
                        when (val data = state.data) {
                            is com.fintrack.app.data.response.LoginResponse -> { // Verifikasi email berhasil
                                navigateToSignIn()
                            }
                            is com.fintrack.app.data.response.VerifyOtpResponse -> { // OTP reset password berhasil
                                Toast.makeText(this@OTPVerificationActivity, data.message, Toast.LENGTH_LONG).show()
                                navigateToResetPassword()
                            }
                        }
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        Toast.makeText(this@OTPVerificationActivity, state.errorMessage, Toast.LENGTH_LONG).show()
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun resendOtp() {
        lifecycleScope.launch {
            repository.resendOtp(userEmail, otpType.value).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        Toast.makeText(this@OTPVerificationActivity, state.data.message, Toast.LENGTH_LONG).show()
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        Toast.makeText(this@OTPVerificationActivity, state.errorMessage, Toast.LENGTH_LONG).show()
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun setupOTPInput() {
        otpEditTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < otpEditTexts.lastIndex) {
                        otpEditTexts[index + 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        otpEditTexts[index - 1].requestFocus()
                    }
                }
                false
            }
        }
    }

    private fun navigateToResetPassword() {
        val intent = Intent(this, ResetPasswordActivity::class.java).apply {
            putExtra("email", userEmail)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        // Implementasi ProgressBar Anda
        // binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        binding.btnSubmitOtp.isEnabled = !isLoading
        binding.tvResendOtp.isEnabled = !isLoading
    }
}
