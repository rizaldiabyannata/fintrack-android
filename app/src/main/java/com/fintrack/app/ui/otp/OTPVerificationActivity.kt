package com.fintrack.app.ui.otp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.ui.profile.ProfileActivity
import com.fintrack.app.ui.resetPassword.ResetPasswordActivity
import com.fintrack.app.R
import com.fintrack.app.service.AuthApiService
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OTPVerificationActivity : AppCompatActivity() {

    // Views
    private lateinit var tvEnterOtp: TextView
    private lateinit var tvOtpDescription: TextView
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvResendOtp: TextView

    // Services
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authApiService: AuthApiService

    // Data
    private lateinit var userEmail: String
    private lateinit var otpEditTexts: List<EditText>
    private lateinit var otpType: OTPType

    enum class OTPType(val value: String) {
        EMAIL_VERIFICATION("email_verification"),
        RESET_PASSWORD("reset_password")
    }

    companion object {
        private const val TAG = "OTPVerificationActivity"
        private const val PREFS_NAME = "user_prefs"
        private const val BASE_URL = "http://192.168.1.13:3000/"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_OTP_TYPE = "extra_otp_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Get email and OTP type from intent
        userEmail = intent.getStringExtra(EXTRA_EMAIL) ?: ""
        val otpTypeString = intent.getStringExtra(EXTRA_OTP_TYPE) ?: OTPType.EMAIL_VERIFICATION.value

        otpType = when (otpTypeString) {
            OTPType.RESET_PASSWORD.value -> OTPType.RESET_PASSWORD
            else -> OTPType.EMAIL_VERIFICATION
        }

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        initializeServices()
        setupOTPInput()
        setupClickListeners()
        setupDescription()
        setupUIBasedOnType()
    }

    private fun initializeViews() {
        tvEnterOtp = findViewById(R.id.tv_enter_otp)
        tvOtpDescription = findViewById(R.id.tv_otp_description)
        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        btnSubmit = findViewById(R.id.btn_create_account)
        tvResendOtp = findViewById(R.id.tv_create_account)

        otpEditTexts = listOf(otp1, otp2, otp3, otp4, otp5)
    }

    private fun initializeServices() {
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApiService = retrofit.create(AuthApiService::class.java)
    }

    private fun setupUIBasedOnType() {
        when (otpType) {
            OTPType.EMAIL_VERIFICATION -> {
                tvEnterOtp.text = "Email Verification"
                btnSubmit.text = "Verify Email"
            }
            OTPType.RESET_PASSWORD -> {
                tvEnterOtp.text = "Reset Password"
                btnSubmit.text = "Verify OTP"
            }
        }
    }

    private fun setupDescription() {
        val maskedEmail = maskEmail(userEmail)
        val descriptionText = when (otpType) {
            OTPType.EMAIL_VERIFICATION -> "We've sent a 5-digit verification code to $maskedEmail"
            OTPType.RESET_PASSWORD -> "We've sent a 5-digit reset code to $maskedEmail"
        }
        tvOtpDescription.text = descriptionText
    }

    private fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email

        val username = parts[0]
        val domain = parts[1]

        return if (username.length <= 2) {
            "$username***@$domain"
        } else {
            "${username.substring(0, 2)}***@$domain"
        }
    }

    private fun setupOTPInput() {
        // Setup auto focus and validation for each OTP EditText
        otpEditTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < otpEditTexts.size - 1) {
                        // Move to next EditText
                        otpEditTexts[index + 1].requestFocus()
                    }

                    // Check if all fields are filled
                    checkAllFieldsFilled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Handle backspace
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        // Move to previous EditText
                        otpEditTexts[index - 1].requestFocus()
                        otpEditTexts[index - 1].text.clear()
                    }
                }
                false
            }
        }

        // Focus on first EditText
        otp1.requestFocus()
    }

    private fun checkAllFieldsFilled() {
        val allFilled = otpEditTexts.all { it.text.toString().isNotEmpty() }
        btnSubmit.isEnabled = allFilled
    }

    private fun setupClickListeners() {
        btnSubmit.setOnClickListener {
            submitOTP()
        }

        tvResendOtp.setOnClickListener {
            resendOTP()
        }
    }

    private fun getOTPString(): String {
        return otpEditTexts.joinToString("") { it.text.toString() }
    }

    private fun clearOTP() {
        otpEditTexts.forEach { it.text.clear() }
        otp1.requestFocus()
    }

    private fun submitOTP() {
        val otpString = getOTPString()

        if (otpString.length != 5) {
            Toast.makeText(this, "Please enter all 5 digits", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        when (otpType) {
            OTPType.EMAIL_VERIFICATION -> verifyEmailOTP(otpString)
            OTPType.RESET_PASSWORD -> verifyResetPasswordOTP(otpString)
        }
    }

    private fun verifyEmailOTP(otpString: String) {
        val otpData = mapOf(
            "email" to userEmail,
            "otp" to otpString
        )

        Log.d(TAG, "Submitting Email Verification OTP: $otpString for email: $userEmail")

        authApiService.verifyEmailOTP(otpData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)
                handleEmailVerificationResponse(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@OTPVerificationActivity, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Email verification OTP API call failed", t)
            }
        })
    }

    private fun verifyResetPasswordOTP(otpString: String) {
        val otpData = mapOf(
            "email" to userEmail,
            "otp" to otpString
        )

        Log.d(TAG, "Submitting Reset Password OTP: $otpString for email: $userEmail")

        // Gunakan endpoint yang berbeda untuk reset password OTP
        authApiService.verifyResetPasswordOTP(otpData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)
                handleResetPasswordOTPResponse(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@OTPVerificationActivity, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Reset password OTP API call failed", t)
            }
        })
    }

    private fun handleEmailVerificationResponse(response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                try {
                    val jsonResponse = JSONObject(responseBody.string())
                    val message = jsonResponse.optString("message", "Email verified successfully")

                    Log.d(TAG, "Email verification successful: $message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    // Parse and save user data
                    val userObject = jsonResponse.optJSONObject("user")
                    userObject?.let {
                        saveUserToPrefs(it)
                    }

                    // Navigate to main activity
                    navigateToProfile()

                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing email verification response", e)
                    Toast.makeText(this, "Email verified successfully", Toast.LENGTH_SHORT).show()
                    navigateToProfile()
                }
            }
        } else {
            handleOTPError(response)
        }
    }

    private fun handleResetPasswordOTPResponse(response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                try {
                    val jsonResponse = JSONObject(responseBody.string())
                    val message = jsonResponse.optString("message", "OTP verified successfully")

                    // Save reset token jika ada
                    val resetToken = jsonResponse.optString("reset_token")
                    if (resetToken.isNotEmpty()) {
                        saveResetToken(resetToken)
                    }

                    Log.d(TAG, "Reset password OTP verification successful: $message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    // Navigate to reset password screen
                    navigateToResetPassword()

                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing reset password OTP response", e)
                    Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show()
                    navigateToResetPassword()
                }
            }
        } else {
            handleOTPError(response)
        }
    }

    private fun handleOTPError(response: Response<ResponseBody>) {
        try {
            response.errorBody()?.let { errorBody ->
                val errorJson = JSONObject(errorBody.string())
                val errorMessage = errorJson.optString("message", "Invalid OTP")
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                if (errorMessage.contains("Invalid") || errorMessage.contains("expired")) {
                    clearOTP()
                }
            } ?: run {
                Toast.makeText(this, "Invalid or expired OTP", Toast.LENGTH_SHORT).show()
                clearOTP()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid or expired OTP", Toast.LENGTH_SHORT).show()
            clearOTP()
        }
    }

    private fun resendOTP() {
        showLoading(true)

        val emailData = mapOf("email" to userEmail)

        Log.d(TAG, "Resending OTP for email: $userEmail, type: ${otpType.value}")

        val apiCall = when (otpType) {
            OTPType.EMAIL_VERIFICATION -> authApiService.resendEmailVerification(emailData)
            OTPType.RESET_PASSWORD -> authApiService.resendEmailVerification(emailData)
        }

        apiCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody.string())
                            val message = jsonResponse.optString("message", "OTP sent successfully")
                            Toast.makeText(this@OTPVerificationActivity, message, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "OTP resent successfully")

                            // Clear current OTP input
                            clearOTP()

                        } catch (e: Exception) {
                            Toast.makeText(this@OTPVerificationActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                            clearOTP()
                        }
                    }
                } else {
                    try {
                        response.errorBody()?.let { errorBody ->
                            val errorJson = JSONObject(errorBody.string())
                            val errorMessage = errorJson.optString("message", "Failed to send OTP")
                            Toast.makeText(this@OTPVerificationActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(this@OTPVerificationActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@OTPVerificationActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@OTPVerificationActivity, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Resend OTP API call failed", t)
            }
        })
    }

    private fun saveUserToPrefs(userObject: JSONObject) {
        val editor = sharedPreferences.edit()
        editor.putString("uid", userObject.optString("uid"))
        editor.putString("email", userObject.optString("email"))
        editor.putString("name", userObject.optString("name"))
        editor.putString("provider", userObject.optString("provider"))
        editor.putBoolean("emailVerified", true)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        Log.d(TAG, "User data saved to preferences")
    }

    private fun saveResetToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("reset_token", token)
        editor.putString("reset_email", userEmail)
        editor.apply()
        Log.d(TAG, "Reset token saved to preferences")
    }

    private fun navigateToProfile() {
        startActivity(
            Intent(this, ProfileActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun navigateToResetPassword() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.putExtra("email", userEmail)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        btnSubmit.isEnabled = !isLoading && otpEditTexts.all { it.text.toString().isNotEmpty() }
        tvResendOtp.isEnabled = !isLoading

        otpEditTexts.forEach { it.isEnabled = !isLoading }

        if (isLoading) {
            btnSubmit.text = "Verifying..."
        } else {
            btnSubmit.text = when (otpType) {
                OTPType.EMAIL_VERIFICATION -> "Verify Email"
                OTPType.RESET_PASSWORD -> "Verify OTP"
            }
        }
    }

    override fun onBackPressed() {
        // Clear OTP when back is pressed
        clearOTP()
        super.onBackPressed()
    }
}