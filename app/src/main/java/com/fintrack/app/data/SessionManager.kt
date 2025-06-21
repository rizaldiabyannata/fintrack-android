package com.fintrack.app.data

import android.content.SharedPreferences
import com.fintrack.app.data.response.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mengelola sesi pengguna, seperti menyimpan dan mengambil data pengguna dari SharedPreferences.
 * Disediakan oleh Hilt sebagai Singleton.
 */
@Singleton
class SessionManager @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val KEY_UID = "uid"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PROVIDER = "provider"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val TOKEN = "token"
        const val REFRESHTOKEN = "refreshToken"
    }

    /**
     * Menyimpan data pengguna dari respons API ke SharedPreferences.
     */
    fun saveUserSession(user: User) {
        val editor = prefs.edit()
        editor.putString(KEY_UID, user.uid)
        editor.putString(KEY_NAME, user.name)
        editor.putString(KEY_EMAIL, user.email)
        editor.putString(KEY_PROVIDER, user.provider)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_NAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Menghapus semua data sesi saat logout.
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
