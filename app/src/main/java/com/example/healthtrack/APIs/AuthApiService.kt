package com.example.healthtrack.APIs

import com.example.healthtrack.ApiDataClasses.AuthResponse
import com.example.healthtrack.ApiDataClasses.LoginRequest
import com.example.healthtrack.ApiDataClasses.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("user/signup")
    suspend fun signUp(
        @Header("Accept") accept: String = "application/json",
        @Body request: SignUpRequest
    ): Response<AuthResponse>

    @POST("user/signin")
    suspend fun login(
        @Header("Accept") accept: String = "application/json",
        @Body request: LoginRequest
    ): Response<AuthResponse>
}