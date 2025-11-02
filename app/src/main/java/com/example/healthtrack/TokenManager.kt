package com.example.healthtrack
import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }

    fun saveAuthData(accessToken: String, email: String, name: String) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, "Bearer $accessToken")
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun clearAuthData() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAccessToken().isNullOrBlank()
    }
}