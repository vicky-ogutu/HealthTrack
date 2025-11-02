package com.example.healthtrack.ApiDataClasses

data class SignUpRequest(
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String
)