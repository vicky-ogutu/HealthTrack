package com.example.healthtrack.ApiDataClasses

data class AuthData(
    val proceed: Int? = null,
    val message: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val updated_at: String? = null,
    val created_at: String? = null,
    val access_token: String? = null
)