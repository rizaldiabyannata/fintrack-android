package com.fintrack.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fintrack.app.data.AuthRepository
import com.fintrack.app.data.SessionManager
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.databinding.FragmentProfileBinding
import com.fintrack.app.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()
        setupListeners()
    }

    private fun loadUserProfile() {
        binding.tvUserName.text = sessionManager.getUserName() ?: "Pengguna"
        binding.tvUserEmail.text = sessionManager.getUserEmail() ?: "Email tidak tersedia"

        // Sesuai permintaan, tidak ada kode untuk memuat gambar dari URL.
        // Ikon default dari XML (@drawable/ic_person) akan digunakan.
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            handleLogout()
        }
    }

    private fun handleLogout() {
        // Pengecekan token tetap di sini sebagai fallback, meskipun seharusnya tidak terjadi.
        if (sessionManager.getRefreshToken() == null) {
            Toast.makeText(context, "Sesi tidak ditemukan.", Toast.LENGTH_SHORT).show()
            logoutClientSide()
            return
        }

        lifecycleScope.launch {
            // DIUBAH: Memanggil logout() tanpa parameter
            authRepository.logout().collect { response ->
                when(response) {
                    is ApiResponse.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.btnLogout.isEnabled = false
                    }
                    is ApiResponse.Success -> {
                        // Logout berhasil di server, bersihkan sesi lokal dan arahkan ke login
                        Toast.makeText(context, response.data.message ?: "Logout berhasil", Toast.LENGTH_SHORT).show()
                        logoutClientSide()
                    }
                    is ApiResponse.Error -> {
                        // Logout gagal di server, beri tahu pengguna tapi tetap logout dari sisi client
                        Toast.makeText(context, "Logout gagal: ${response.errorMessage}", Toast.LENGTH_LONG).show()
                        logoutClientSide()
                    }
                }
            }
        }
    }

    private fun logoutClientSide() {
        sessionManager.clearSession()
        navigateToSignIn()
    }

    private fun navigateToSignIn() {
        activity?.let {
            val intent = Intent(it, SignInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            it.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
