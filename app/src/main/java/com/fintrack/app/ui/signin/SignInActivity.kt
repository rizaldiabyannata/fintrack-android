package com.fintrack.app.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.AuthRepository
import com.fintrack.app.data.SessionManager
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.LoginRequest
import com.fintrack.app.data.request.UserPayload
import com.fintrack.app.databinding.ActivitySignInBinding
import com.fintrack.app.ui.navigation.NavigationActivity
import com.fintrack.app.ui.otp.OTPVerificationActivity
import com.fintrack.app.ui.signup.SignUpActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var repository: AuthRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val TAG = "SignInActivity"
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        showLoading(false) // Stop loading once the Google Sign-In UI is closed
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                displayToast("Google sign in failed. Code: ${e.statusCode}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check session using SessionManager, which is now the single source of truth
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
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
        binding.btnGoogle.setOnClickListener {
            showLoading(true)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        binding.btnSignin.setOnClickListener {
            val email = binding.etEmailPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signInWithEmailPassword(LoginRequest(email, password))
            }
        }

        binding.forgotPasswordSignIn.setOnClickListener {
            val email = binding.etEmailPhone.text.toString().trim()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                requestPasswordReset(email)
            } else {
                binding.etEmailPhone.error = "Masukkan email yang valid untuk reset password"
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun requestPasswordReset(email: String){
        lifecycleScope.launch {
            repository.requestPasswordReset(email).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        displayToast(state.data.message ?: "OTP sent successfully")
                        // Arahkan ke OTP setelah permintaan berhasil
                        val intent = Intent(this@SignInActivity, OTPVerificationActivity::class.java).apply {
                            putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
                            putExtra(OTPVerificationActivity.EXTRA_OTP_TYPE, OTPVerificationActivity.OTPType.RESET_PASSWORD)
                        }
                        startActivity(intent)
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        displayToast(state.errorMessage)
                    }
                }
            }
        }
    }

    private fun signInWithEmailPassword(loginRequest: LoginRequest) {
        lifecycleScope.launch {
            repository.login(loginRequest).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        val user = state.data.user
                        if (user != null) {
                            sessionManager.saveUserSession(user)
                            displayToast("Welcome back, ${user.name}!")
                            navigateToMain()
                        } else {
                            displayToast("Gagal mendapatkan data pengguna.")
                        }
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        // Backend now sends a more specific message for unverified email.
                        if (state.errorMessage.contains("Please verify your email")) {
                            navigateToOTPVerification(loginRequest.email)
                        } else {
                            displayToast(state.errorMessage)
                        }
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoading(true) // Show loading indicator while authenticating with Firebase
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseToken = tokenTask.result?.token
                        if (firebaseToken != null && user != null) {
                            val payload = UserPayload(user.uid, user.displayName ?: "", user.email ?: "")
                            loginWithGoogle(firebaseToken, payload)
                        } else {
                            showLoading(false)
                            displayToast("Gagal mendapatkan token otentikasi.")
                        }
                    } else {
                        showLoading(false)
                        displayToast("Gagal mendapatkan token otentikasi.")
                    }
                }
            } else {
                showLoading(false)
                displayToast("Otentikasi Firebase gagal.")
            }
        }
    }

    private fun loginWithGoogle(token: String, payload: UserPayload) {
        lifecycleScope.launch {
            repository.loginWithGoogle(token, payload).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> { /* Loading handled in firebaseAuthWithGoogle */ }
                    is ApiResponse.Success -> {
                        showLoading(false)
                        val user = state.data.user
                        if (user != null) {
                            sessionManager.saveUserSession(user)
                            displayToast("Welcome, ${user.name}!")
                            navigateToMain()
                        } else {
                            displayToast("Gagal mendapatkan data pengguna dari server.")
                        }
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        displayToast(state.errorMessage)
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.etEmailPhone.error = null
        binding.etPassword.error = null

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailPhone.error = "Masukkan email yang valid"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            return false
        }
        return true
    }

    private fun navigateToOTPVerification(email: String) {
        val intent = Intent(this, OTPVerificationActivity::class.java).apply {
            putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
            putExtra(OTPVerificationActivity.EXTRA_OTP_TYPE, OTPVerificationActivity.OTPType.EMAIL_VERIFICATION)
        }
        startActivity(intent)
    }

    private fun navigateToMain() {
        val intent = Intent(this, NavigationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnSignin.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
