package com.fintrack.app.ui.signin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.data.UserPayload
import com.fintrack.app.service.AuthApiService
import com.fintrack.app.ui.otp.OTPVerificationActivity
import com.fintrack.app.ui.profile.ProfileActivity
import com.fintrack.app.ui.signup.SignUpActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInActivity : AppCompatActivity() {

    // Initialize variables
    private lateinit var btnGoogleSignIn: MaterialButton
    private lateinit var btnSignIn: MaterialButton
    private lateinit var etEmailPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvCreateAccount: TextView

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val TAG = "SignInActivity"
        private const val PREFS_NAME = "user_prefs"
        private const val BASE_URL = "http://18.142.179.208:3000/"
    }

    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApiService = retrofit.create(AuthApiService::class.java)

    // Launcher untuk result login Google
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                displayToast("Login Google berhasil: ${account.displayName}")

                // Autentikasi ke Firebase
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { authTask ->
                        if (authTask.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            displayToast("Firebase Auth berhasil: ${user?.email}")

                            user?.getIdToken(true)?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val idToken = task.result?.token
                                    Log.d(TAG, "Token baru: $idToken")
                                    postGoogleUserData(user?.uid, user?.displayName, user?.email, idToken)
                                } else {
                                    Log.e(TAG, "Gagal ambil token: ${task.exception?.message}")
                                    saveUserToPrefs(user)
                                    navigateToMain()
                                }
                            }
                        } else {
                            displayToast("Firebase Auth gagal: ${authTask.exception?.message}")
                        }
                    }

            } catch (e: ApiException) {
                displayToast("Google Sign-In error: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize views
        initializeViews()

        // Initialize services
        initializeServices()

        // Check if user already logged in
        checkUserLoggedIn()

        // Setup click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        btnGoogleSignIn = findViewById(R.id.btn_google)
        btnSignIn = findViewById(R.id.btn_create_account) // Sign In button
        etEmailPhone = findViewById(R.id.et_email_phone)
        etPassword = findViewById(R.id.et_password)
        tvForgotPassword = findViewById(R.id.forgotPasswordSignIn)
        tvCreateAccount = findViewById(R.id.tv_create_account)
    }

    private fun initializeServices() {
        // Konfigurasi opsi Google Sign-In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Inisialisasi GoogleSignInClient dan Firebase
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private fun checkUserLoggedIn() {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (firebaseUser != null && isLoggedIn) {
            navigateToMain()
        }
    }

    private fun setupClickListeners() {
        // Google Sign In
        btnGoogleSignIn.setOnClickListener {
            signInLauncher.launch(googleSignInClient.signInIntent)
        }

        // Email/Password Sign In
        btnSignIn.setOnClickListener {
            val email = etEmailPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signInWithEmailPassword(email, password)
            }
        }

        // Forgot Password
        tvForgotPassword.setOnClickListener {
            val email = etEmailPhone.text.toString().trim()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                resetPassword(email)
            } else {
                displayToast("Please enter a valid email address first")
            }
        }

        // Navigate to Sign Up
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmailPhone.error = "Email is required"
            etEmailPhone.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailPhone.error = "Please enter a valid email"
            etEmailPhone.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun signInWithEmailPassword(email: String, password: String) {
        showLoading(true)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = firebaseAuth.currentUser

                    // Check if email is verified
                    if (user?.isEmailVerified == false) {
                        showLoading(false)
                        // Navigate to OTP verification activity
                        navigateToOTPVerification(email)
                        return@addOnCompleteListener
                    }

                    // Continue with normal login flow...
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        showLoading(false)
                        if (tokenTask.isSuccessful) {
                            val idToken = tokenTask.result?.token
                            idToken?.let { token ->
                                sendEmailLoginToBackend(token)
                            } ?: run {
                                saveUserToPrefs(user)
                                navigateToMain()
                            }
                        } else {
                            Log.e(TAG, "Failed to get ID token: ${tokenTask.exception?.message}")
                            saveUserToPrefs(user)
                            navigateToMain()
                        }
                    }
                } else {
                    showLoading(false)
                    // Handle login errors...
                    val errorMessage = when (task.exception?.message) {
                        "The email address is badly formatted." -> "Invalid email format"
                        "The password is invalid or the user does not have a password." -> "Invalid password"
                        "There is no user record corresponding to this identifier. The user may have been deleted." -> "No account found with this email"
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Network error. Please check your connection"
                        else -> "Authentication failed. Please try again."
                    }
                    displayToast(errorMessage)
                }
            }
    }

    private fun navigateToOTPVerification(email: String) {
        val intent = Intent(this, OTPVerificationActivity::class.java)
        intent.putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
        intent.putExtra(OTPVerificationActivity.EXTRA_OTP_TYPE, OTPVerificationActivity.OTPType.EMAIL_VERIFICATION.value)
        startActivity(intent)
        // Don't finish this activity, user might want to go back
    }

    private fun showEmailVerificationDialog(email: String) {
        AlertDialog.Builder(this)
            .setTitle("Email Verification Required")
            .setMessage("Please verify your email before signing in. Check your inbox for the verification email.")
            .setPositiveButton("Resend Email") { _, _ ->
                resendVerificationEmailFirebase()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("I've Verified") { _, _ ->
                checkEmailVerificationStatus()
            }
            .setCancelable(false)
            .show()
    }

    private fun resendVerificationEmailFirebase() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val emailData = mapOf("email" to (currentUser.email ?: ""))

            if (currentUser.email.isNullOrEmpty()) {
                displayToast("Email not found. Please try signing in again.")
                return
            }

            // Hit API endpoint instead of Firebase client
            authApiService.resendEmailVerification(emailData).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            try {
                                val jsonResponse = JSONObject(responseBody.string())
                                val message = jsonResponse.optString("message", "Verification email sent successfully")
                                displayToast(message)
                                Log.d(TAG, "Verification email sent to: ${currentUser.email}")
                            } catch (e: Exception) {
                                displayToast("Verification email sent successfully")
                                Log.d(TAG, "Verification email sent to: ${currentUser.email}")
                            }
                        }
                    } else {
                        // Handle specific error codes
                        try {
                            response.errorBody()?.let { errorBody ->
                                val errorJson = JSONObject(errorBody.string())
                                val errorMessage = errorJson.optString("message", "Failed to send verification email")

                                when (response.code()) {
                                    400 -> {
                                        if (errorMessage.contains("already verified")) {
                                            displayToast("Email is already verified")
                                            // Auto check verification status
                                            checkEmailVerificationStatus()
                                        } else {
                                            displayToast(errorMessage)
                                        }
                                    }
                                    404 -> displayToast("User not found. Please register first.")
                                    else -> displayToast(errorMessage)
                                }
                            } ?: run {
                                displayToast("Failed to send verification email")
                            }
                        } catch (e: Exception) {
                            displayToast("Failed to send verification email")
                            Log.e(TAG, "Error parsing error response", e)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    displayToast("Network error. Please check your connection.")
                    Log.e(TAG, "Resend verification API call failed", t)
                }
            })
        } else {
            displayToast("User not found. Please try signing in again.")
        }
    }

    private fun checkEmailVerificationStatus() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    if (currentUser.isEmailVerified) {
                        displayToast("Email verified successfully!")

                        // Continue with login process
                        currentUser.getIdToken(true).addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val idToken = tokenTask.result?.token
                                idToken?.let { token ->
                                    sendEmailLoginToBackend(token)
                                } ?: run {
                                    saveUserToPrefs(currentUser)
                                    navigateToMain()
                                }
                            } else {
                                saveUserToPrefs(currentUser)
                                navigateToMain()
                            }
                        }
                    } else {
                        displayToast("Email not verified yet. Please check your inbox.")
                    }
                } else {
                    displayToast("Failed to refresh verification status")
                    Log.e(TAG, "Failed to reload user", reloadTask.exception)
                }
            }
        }
    }

    private fun sendEmailLoginToBackend(idToken: String) {
        val loginData = mapOf("idToken" to idToken)

        authApiService.loginUser(loginData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody.string())
                            val message = jsonResponse.optString("message", "Login successful")

                            Log.d(TAG, "Backend login successful: $message")

                            // Parse and save user data
                            val userObject = jsonResponse.optJSONObject("user")
                            userObject?.let {
                                saveUserToPrefs(it)
                            }

                            displayToast("Welcome back!")
                            navigateToMain()

                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing login response", e)
                            saveUserToPrefs(firebaseAuth.currentUser)
                            navigateToMain()
                        }
                    }
                } else {
                    Log.e(TAG, "Backend login failed: ${response.code()} - ${response.message()}")
                    saveUserToPrefs(firebaseAuth.currentUser)
                    navigateToMain()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Backend login API call failed", t)
                saveUserToPrefs(firebaseAuth.currentUser)
                navigateToMain()
            }
        })
    }

    private fun resetPassword(email: String) {
        // Menampilkan loading saat request dikirim
        showLoading(true)

        // Memanggil API reset password dengan email
        authApiService.resetPassword(mapOf("email" to email)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false) // Menyembunyikan loading setelah response diterima

                if (response.isSuccessful) {
                    // OTP berhasil dikirim, sekarang arahkan pengguna ke OTP Verification Activity
                    displayToast("Password reset email sent to $email")
                    Log.d(TAG, "Password reset email sent to: $email")

                    // Mengirim email ke OTP Verification Activity
                    val intent = Intent(this@SignInActivity, OTPVerificationActivity::class.java)
                    intent.putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
                    intent.putExtra(OTPVerificationActivity.EXTRA_OTP_TYPE, OTPVerificationActivity.OTPType.RESET_PASSWORD.value)
                    startActivity(intent)
                } else {
                    // Tangani kegagalan response dari server
                    val errorMessage = response.errorBody()?.string() ?: "Failed to send reset email"
                    displayToast(errorMessage)
                    Log.e(TAG, "Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false) // Menyembunyikan loading jika terjadi error
                displayToast("Network error. Please check your connection.")
                Log.e(TAG, "Failed to send password reset email", t)
            }
        })
    }


    private fun postGoogleUserData(uid: String?, name: String?, email: String?, token: String?) {
        if (token.isNullOrEmpty()) {
            displayToast("Token tidak tersedia!")
            saveUserToPrefs(firebaseAuth.currentUser)
            navigateToMain()
            return
        }

        val userPayload = UserPayload(uid.toString(), email.toString(), name.toString(), "google")
        val authHeader = "Bearer $token"

        authApiService.postUserData(authHeader, userPayload).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            try {
                                val jsonResponse = JSONObject(responseBody.string())
                                val userObject = jsonResponse.optJSONObject("user")
                                userObject?.let {
                                    saveUserToPrefs(it)
                                }
                                displayToast("Login successful!")
                                Log.d(TAG, "Google user data posted successfully")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing Google response", e)
                                saveUserToPrefs(firebaseAuth.currentUser)
                            }
                        }
                    } else {
                        Log.e(TAG, "Google backend failed: ${response.message()}")
                        saveUserToPrefs(firebaseAuth.currentUser)
                    }
                    navigateToMain()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                runOnUiThread {
                    Log.e(TAG, "Google API call failed: ${t.message}")
                    saveUserToPrefs(firebaseAuth.currentUser)
                    navigateToMain()
                }
            }
        })
    }

    private fun saveUserToPrefs(userObject: JSONObject) {
        val editor = sharedPreferences.edit()
        editor.putString("uid", userObject.optString("uid"))
        editor.putString("email", userObject.optString("email"))
        editor.putString("name", userObject.optString("name"))
        editor.putString("provider", userObject.optString("provider"))
        editor.putBoolean("emailVerified", userObject.optBoolean("emailVerified", false))
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        Log.d(TAG, "User data saved to preferences")
    }

    private fun saveUserToPrefs(firebaseUser: FirebaseUser?) {
        firebaseUser?.let { user ->
            val editor = sharedPreferences.edit()
            editor.putString("uid", user.uid)
            editor.putString("email", user.email)
            editor.putString("name", user.displayName ?: user.email?.split("@")?.get(0))
            editor.putString("provider", "email")
            editor.putBoolean("emailVerified", user.isEmailVerified)
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            Log.d(TAG, "Firebase user data saved to preferences")
        }
    }

    private fun navigateToMain() {
        startActivity(
            Intent(this@SignInActivity, ProfileActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        btnSignIn.isEnabled = !isLoading
        btnGoogleSignIn.isEnabled = !isLoading
        etEmailPhone.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading

        if (isLoading) {
            btnSignIn.text = "Signing In..."
        } else {
            btnSignIn.text = "Sign In"
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User already signed in: ${currentUser.email}")
            // User sudah login, tapi tetap check di checkUserLoggedIn() method
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear any pending authentication operations
        Log.d(TAG, "SignInActivity destroyed")
    }

    // Method untuk clear semua field input
    private fun clearInputFields() {
        etEmailPhone.text?.clear()
        etPassword.text?.clear()
        etEmailPhone.error = null
        etPassword.error = null
    }

    // Method untuk handle back press
    override fun onBackPressed() {
        // Clear sensitive data before going back
        clearInputFields()
        super.onBackPressed()
    }
}