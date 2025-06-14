package com.fintrack.app.ui.resetPassword

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.service.AuthApiService
import com.fintrack.app.ui.signin.SignInActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var authApiService: AuthApiService
    private lateinit var sharedPreferences: SharedPreferences

    // Views
    private lateinit var backButton: MaterialButton
    private lateinit var submitButton: MaterialButton
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tvResetPasswordInput: TextInputLayout
    private lateinit var tvResetPasswordConfirmation: TextInputLayout

    // Data
    private var userEmail: String = ""
    private var resetToken: String = ""

    companion object {
        private const val TAG = "ResetPasswordActivity"
        private const val PREFS_NAME = "user_prefs"
        private const val BASE_URL = "http://18.142.179.208:3000/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initViews()
        setupApi()
        setupViews()
        setupClickListeners()
        getDataFromIntent()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        submitButton = findViewById(R.id.submit_button)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        tvResetPasswordInput = findViewById(R.id.tv_reset_password_input)
        tvResetPasswordConfirmation = findViewById(R.id.tv_reset_password_confirmation)
    }

    private fun setupApi() {
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Initialize API service
        authApiService = RetrofitClient.getApiService()
    }

    private fun setupViews() {
        // Set proper input types for password fields
        etNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun setupClickListeners() {
        // Back button click
        backButton.setOnClickListener {
            finish()
        }

        // Submit button click
        submitButton.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun getDataFromIntent() {
        // Get email from intent (passed from OTP verification)
        userEmail = intent.getStringExtra("email") ?: ""

        // Get reset token from SharedPreferences (saved during OTP verification)
        resetToken = sharedPreferences.getString("reset_token", "") ?: ""

        // If no email from intent, try to get from SharedPreferences
        if (userEmail.isEmpty()) {
            userEmail = sharedPreferences.getString("reset_email", "") ?: ""
        }

        Log.d(TAG, "Email: $userEmail, Reset Token: ${if (resetToken.isNotEmpty()) "Present" else "Not Found"}")

        // Validate required data
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Email not found. Please restart the reset process.", Toast.LENGTH_LONG).show()
            navigateToLogin()
            return
        }
    }

    private fun validateAndSubmit() {
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Reset previous errors
        tvResetPasswordInput.error = null
        tvResetPasswordConfirmation.error = null

        if (!validatePasswords(newPassword, confirmPassword)) {
            return
        }

        // Proceed with API call
        resetPassword(newPassword, confirmPassword)
    }

    private fun validatePasswords(newPassword: String, confirmPassword: String): Boolean {
        var isValid = true

        // Check if new password is empty
        if (newPassword.isEmpty()) {
            tvResetPasswordInput.error = "Password cannot be empty"
            isValid = false
        }

        // Check password length
        else if (newPassword.length < 6) {
            tvResetPasswordInput.error = "Password must be at least 6 characters"
            isValid = false
        }

        // Check password strength (optional)
        else if (!isPasswordStrong(newPassword)) {
            tvResetPasswordInput.error = "Password must contain at least one uppercase, lowercase, and number"
            isValid = false
        }

        // Check if confirm password is empty
        if (confirmPassword.isEmpty()) {
            tvResetPasswordConfirmation.error = "Please confirm your password"
            isValid = false
        }

        // Check if passwords match
        else if (newPassword != confirmPassword) {
            tvResetPasswordConfirmation.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    private fun isPasswordStrong(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUppercase && hasLowercase && hasDigit
    }

    private fun resetPassword(newPassword: String, confirmPassword: String) {
        // Show loading
        showLoading(true)

        // Prepare request data - sesuaikan dengan kebutuhan API
        val passwordData = mutableMapOf(
            "email" to userEmail,
            "new_password" to newPassword,
            "confirm_password" to confirmPassword
        )

        // Add reset token if available
        if (resetToken.isNotEmpty()) {
            passwordData["reset_token"] = resetToken
        }

        Log.d(TAG, "Submitting password reset for email: $userEmail")

        // Make API call using AuthApiService
        authApiService.setNewPassword(passwordData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)

                if (response.isSuccessful) {
                    handleSuccessResponse(response)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                showErrorMessage("Network error: ${t.message}")
                Log.e(TAG, "Reset password API call failed", t)
            }
        })
    }

    private fun handleSuccessResponse(response: Response<ResponseBody>) {
        try {
            response.body()?.let { responseBody ->
                val jsonResponse = JSONObject(responseBody.string())
                val message = jsonResponse.optString("message", "Password reset successfully!")

                Log.d(TAG, "Password reset successful: $message")
                showSuccessMessage(message)

                // Clear reset token from preferences
                clearResetData()

                // Navigate to login screen
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLogin()
                }, 2000)

            } ?: run {
                showSuccessMessage("Password reset successfully!")
                clearResetData()
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLogin()
                }, 2000)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing success response", e)
            showSuccessMessage("Password reset successfully!")
            clearResetData()
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToLogin()
            }, 2000)
        }
    }

    private fun handleErrorResponse(response: Response<ResponseBody>) {
        try {
            val errorBody = response.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Failed to reset password"
            showErrorMessage(errorMessage)

            Log.e(TAG, "Password reset failed: $errorMessage")

            // If token expired, navigate back to forgot password
            if (errorMessage.contains("token", ignoreCase = true) &&
                errorMessage.contains("expired", ignoreCase = true)) {

                Toast.makeText(this, "Reset session expired. Please start over.", Toast.LENGTH_LONG).show()
                clearResetData()

                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLogin()
                }, 2000)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing error response", e)
            showErrorMessage("An error occurred. Please try again.")
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            // Parse error JSON if your API returns structured error messages
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.optString("message") ?: jsonObject.optString("error")
        } catch (e: Exception) {
            null
        }
    }

    private fun clearResetData() {
        val editor = sharedPreferences.edit()
        editor.remove("reset_token")
        editor.remove("reset_email")
        editor.apply()
        Log.d(TAG, "Reset data cleared from preferences")
    }

    private fun showLoading(show: Boolean) {
        submitButton.isEnabled = !show
        etNewPassword.isEnabled = !show
        etConfirmPassword.isEnabled = !show

        submitButton.text = if (show) "Processing..." else "Submit"
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToLogin() {
        // Navigate to login activity
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    object RetrofitClient {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getApiService(): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }
    }
}