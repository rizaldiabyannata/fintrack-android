package com.fintrack.app.ui.signup

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fintrack.app.R
import com.fintrack.app.databinding.ActivitySignUpBinding
import com.fintrack.app.service.AuthApiService
import com.fintrack.app.data.UserPayload
import com.fintrack.app.ui.signin.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authApiService: AuthApiService
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val TAG = "SignUpActivity"
        private const val RC_SIGN_IN = 9001
        private const val PREFS_NAME = "user_prefs"
        private const val BASE_URL = "http://18.142.179.208:3000/" // ganti dengan URL server Anda
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Initialize
        auth = Firebase.auth
        authApiService = retrofit.create(AuthApiService::class.java)
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupClickListeners()
    }

    // ... sisa kode sama seperti sebelumnya
    private fun setupClickListeners() {
        // Sign up with email/password
        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmailPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(name, email, password)) {
                signUpWithEmailPassword(name, email, password)
            }
        }

        // Sign up with Google
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Navigate to Sign In
        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmailPhone.error = "Email is required"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailPhone.error = "Please enter a valid email"
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

    private fun signUpWithEmailPassword(name: String, email: String, password: String) {
        showLoading(true)

        val userPayload = UserPayload(
            email = email,
            password = password,
            name = name,
            provider = "email"
        )

        authApiService.registerUser(userPayload).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody.string())
                            val message = jsonResponse.optString("message", "Registration successful")

                            Log.d(TAG, "Registration successful: $message")
                            Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()

                            val userObject = jsonResponse.optJSONObject("user")
                            userObject?.let {
                                saveUserToPrefs(it)
                            }

                            navigateToMain()

                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing response", e)
                            Toast.makeText(this@SignUpActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                    }
                } else {
                    handleError(response.code(), response.errorBody())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@SignUpActivity, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Registration API call failed", t)
            }
        })
    }

    // ... sisa method sama seperti sebelumnya
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
                    user?.let {
                        it.getIdToken(true).addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val firebaseToken = tokenTask.result?.token
                                firebaseToken?.let { token ->
                                    sendGoogleAuthToBackend(token, it)
                                }
                            } else {
                                showLoading(false)
                                Toast.makeText(this, "Failed to get authentication token", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    showLoading(false)
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendGoogleAuthToBackend(token: String, firebaseUser: FirebaseUser) {
        val userPayload = UserPayload(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            name = firebaseUser.displayName,
            provider = "google"
        )

        authApiService.postUserData("Bearer $token", userPayload).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody.string())
                            val message = jsonResponse.optString("message", "Authentication successful")

                            Log.d(TAG, "Google auth successful: $message")
                            Toast.makeText(this@SignUpActivity, "Welcome!", Toast.LENGTH_SHORT).show()

                            val userObject = jsonResponse.optJSONObject("user")
                            userObject?.let {
                                saveUserToPrefs(it)
                            }

                            navigateToMain()

                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Google auth response", e)
                            Toast.makeText(this@SignUpActivity, "Welcome!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Google auth failed: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@SignUpActivity, "Network error", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Google auth API call failed", t)
            }
        })
    }

    private fun handleError(code: Int, errorBody: ResponseBody?) {
        val errorMessage = try {
            errorBody?.let {
                val errorJson = JSONObject(it.string())
                errorJson.optString("message", "Registration failed")
            } ?: when (code) {
                409 -> "Email already in use"
                400 -> "Invalid email or password"
                else -> "Registration failed. Please try again."
            }
        } catch (e: Exception) {
            when (code) {
                409 -> "Email already in use"
                400 -> "Invalid email or password"
                else -> "Registration failed. Please try again."
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, "Registration failed: $code - $errorMessage")
    }

    private fun saveUserToPrefs(userObject: JSONObject) {
        val editor = sharedPreferences.edit()
        editor.putString("uid", userObject.optString("uid"))
        editor.putString("email", userObject.optString("email"))
        editor.putString("name", userObject.optString("name"))
        editor.putString("provider", userObject.optString("provider"))
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun navigateToMain() {
        startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnCreateAccount.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading

        if (isLoading) {
            binding.btnCreateAccount.text = "Creating Account..."
        } else {
            binding.btnCreateAccount.text = "Create Account"
        }
    }
}