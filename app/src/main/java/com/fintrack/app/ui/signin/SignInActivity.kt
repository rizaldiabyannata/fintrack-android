package com.fintrack.app.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.R
import com.fintrack.app.data.AuthRepository
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.LoginRequest
import com.fintrack.app.data.request.UserPayload
import com.fintrack.app.databinding.ActivitySignInBinding
//import com.fintrack.app.ui.navigation.NavigationActivity //Nanti di sesuai kan ketika navigation telah selesai
import com.fintrack.app.ui.profile.ProfileActivity
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
    lateinit var repository: AuthRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val TAG = "SignInActivity"
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                showLoading(false)
                Log.w(TAG, "Google sign in failed", e)
                displayToast("Google sign in failed")
            }
        } else {
            showLoading(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Langsung cek apakah user sudah login di Firebase
        if (firebaseAuth.currentUser != null && firebaseAuth.currentUser!!.isEmailVerified) {
            navigateToMain()
            return
        }

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

        binding.tvCreateAccount.setOnClickListener {
            val email = binding.etEmailPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signInWithEmailPassword(LoginRequest(email, password))
            }
        }

        binding.forgotPasswordSignIn.setOnClickListener {
            // Logika untuk forgot password bisa ditambahkan di sini
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signInWithEmailPassword(loginRequest: LoginRequest) {
        lifecycleScope.launch {
            repository.login(loginRequest).collect { state ->
                when (state) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        displayToast("Welcome back, ${state.data.user.name}!")
                        navigateToMain()
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        // Jika error karena email belum terverifikasi, arahkan ke OTP
                        if (state.errorMessage.contains("Email belum terverifikasi")) {
                            navigateToOTPVerification(loginRequest.email)
                        } else {
                            displayToast(state.errorMessage)
                        }
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseToken = tokenTask.result?.token
                        if (firebaseToken != null && user != null) {
                            val payload = UserPayload(user.uid, user.displayName ?: "", user.email ?: "", "google")
                            loginWithGoogle(firebaseToken, payload)
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
                    is ApiResponse.Loading -> { /* Loading sudah ditangani */ }
                    is ApiResponse.Success -> {
                        showLoading(false)
                        displayToast("Welcome, ${state.data.user.name}!")
                        navigateToMain()
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        displayToast(state.errorMessage)
                    }
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
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
            putExtra(OTPVerificationActivity.EXTRA_OTP_TYPE, OTPVerificationActivity.OTPType.EMAIL_VERIFICATION.value)
        }
        startActivity(intent)
    }

    private fun navigateToMain() {
        val intent = Intent(this@SignInActivity, ProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        // Ganti dengan ProgressBar jika ada
        binding.btnSignin.isEnabled= !isLoading
        binding.btnGoogle.isEnabled = !isLoading
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
