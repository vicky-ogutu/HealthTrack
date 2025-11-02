package com.example.healthtrack.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.ApiDataClasses.AuthResponse
import com.example.healthtrack.Repositories.AuthRepository
import com.example.healthtrack.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Login State
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    // SignUp State
    private val _signUpState = MutableStateFlow<AuthState>(AuthState.Idle)
    val signUpState: StateFlow<AuthState> = _signUpState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = authRepository.login(
                    com.example.healthtrack.ApiDataClasses.LoginRequest(
                        email = email,
                        password = password
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data
                    authData.access_token?.let { token ->
                        authData.email?.let { email ->
                            authData.name?.let { name ->
                                tokenManager.saveAuthData(token, email, name)
                            }
                        }
                    }
                    _loginState.value = AuthState.Success(response.body()!!)
                } else {
                    _loginState.value = AuthState.Error(
                        response.body()?.data?.message ?: "Login failed"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun signUp(email: String, firstname: String, lastname: String, password: String) {
        viewModelScope.launch {
            _signUpState.value = AuthState.Loading
            try {
                val response = authRepository.signUp(
                    com.example.healthtrack.ApiDataClasses.SignUpRequest(
                        email = email,
                        firstname = firstname,
                        lastname = lastname,
                        password = password
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _signUpState.value = AuthState.Success(response.body()!!)
                } else {
                    _signUpState.value = AuthState.Error(
                        response.body()?.data?.message ?: "Sign up failed"
                    )
                }
            } catch (e: Exception) {
                _signUpState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun clearSignUpState() {
        _signUpState.value = AuthState.Idle
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    fun logout() {
        tokenManager.clearAuthData()
    }

    companion object {
        fun Factory(
            authRepository: AuthRepository,
            tokenManager: TokenManager
        ): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(authRepository, tokenManager) as T
                }
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}