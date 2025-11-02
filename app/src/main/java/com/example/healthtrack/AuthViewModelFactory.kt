package com.example.healthtrack


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthtrack.TokenManager
import com.example.healthtrack.Repositories.AuthRepository
import com.example.healthtrack.ViewModels.AuthViewModel

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}