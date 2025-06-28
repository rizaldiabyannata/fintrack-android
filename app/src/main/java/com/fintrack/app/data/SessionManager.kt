package com.fintrack.app.data

import android.content.SharedPreferences
import com.fintrack.app.data.response.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val KEY_UID = "uid"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PROVIDER = "provider"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_ACCESS_TOKEN = "accessToken" // DIUBAH: lebih deskriptif
        const val KEY_REFRESH_TOKEN = "refreshToken" // DIUBAH: konsisten dengan snake_case
    }

    /**
     * Menyimpan data pengguna dari respons API ke SharedPreferences.
     */
    fun saveUserSession(user: User) {
        val editor = prefs.edit()
        editor.putString(KEY_UID, user.uid) // Aman karena uid sekarang String?
        editor.putString(KEY_NAME, user.name)
        editor.putString(KEY_EMAIL, user.email)
        editor.putString(KEY_PROVIDER, user.provider)
        editor.putString(KEY_ACCESS_TOKEN, user.accessToken) // DIUBAH: dari user.token ke user.accessToken
        editor.putString(KEY_REFRESH_TOKEN, user.refreshToken)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_NAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    // DIUBAH: nama fungsi dan key yang digunakan
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        // Cek juga apakah access token ada, untuk memastikan sesi valid
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAccessToken() != null
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
