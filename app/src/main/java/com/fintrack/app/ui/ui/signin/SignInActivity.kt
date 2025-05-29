package com.fintrack.app.ui.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "SignInActivity"

        // Gunakan 10.0.2.2 untuk emulator Android
        private const val BASE_URL = "http://10.0.2.2:3000/api/auth"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Google Sign In
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Email/Password Sign In
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.etEmailPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signInWithEmailPassword(email, password)
            }
        }

        // Create Account
        binding.tvCreateAccount.setOnClickListener {
            // Navigate to SignUpActivity
            // startActivity(Intent(this, SignUpActivity::class.java))
            Toast.makeText(this, "Create Account clicked", Toast.LENGTH_SHORT).show()
        }

        // Forgot Password
        binding.forgotPasswordSignIn.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmailPhone.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    // Google Sign In
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoading(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    // Get Firebase token and send to backend
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val firebaseToken = tokenTask.result?.token
                            sendTokenToBackend(firebaseToken!!, user.email, user.displayName)
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showLoading(false)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Email/Password Sign In
    private fun signInWithEmailPassword(email: String, password: String) {
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    // Get Firebase token and send to backend
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val firebaseToken = tokenTask.result?.token
                            sendTokenToBackend(firebaseToken!!, email, null)
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showLoading(false)
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Send token to backend
    private fun sendTokenToBackend(firebaseToken: String, email: String?, displayName: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                val json = JSONObject().apply {
                    put("firebaseToken", firebaseToken)
                    put("email", email)
                    put("displayName", displayName)
                }

                val body = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url("$BASE_URL/verify-token")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "{}")

                        // Save backend token or user data if needed
                        val backendToken = jsonResponse.optString("token")
                        saveUserSession(backendToken)

                        // Navigate to main activity (sesuaikan dengan main activity kamu)
                        Toast.makeText(this@SignInActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        // startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        // finish()
                    } else {
                        Toast.makeText(this@SignInActivity,
                            "Backend authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Log.e(TAG, "Backend request failed", e)

                    // For development, show success even if backend fails
                    Toast.makeText(this@SignInActivity,
                        "Dev mode: Login successful (backend offline)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserSession(token: String) {
        val sharedPref = getSharedPreferences("fintrack_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("backend_token", token)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnCreateAccount.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading
        binding.btnCreateAccount.text = if (isLoading) "Loading..." else "Sign In"
    }
}