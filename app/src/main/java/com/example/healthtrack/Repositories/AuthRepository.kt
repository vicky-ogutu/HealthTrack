package com.example.healthtrack.Repositories


import com.example.healthtrack.APIs.AuthApiService
import com.example.healthtrack.ApiDataClasses.AuthResponse
import com.example.healthtrack.ApiDataClasses.LoginRequest
import com.example.healthtrack.ApiDataClasses.SignUpRequest
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    suspend fun signUp(signUpRequest: SignUpRequest): Response<AuthResponse> {
        return authApiService.signUp(request = signUpRequest)
    }

    suspend fun login(loginRequest: LoginRequest): Response<AuthResponse> {
        return authApiService.login(request = loginRequest)
    }
}