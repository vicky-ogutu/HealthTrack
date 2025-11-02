package com.example.healthtrack.ApiDataClasses

data class AuthResponse(
    val message: String,
    val success: Boolean,
    val code: Int,
    val data: AuthData
)