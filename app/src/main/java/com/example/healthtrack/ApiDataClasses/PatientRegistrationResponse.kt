package com.example.healthtrack.ApiDataClasses
data class PatientRegistrationResponse(
    val message: String,
    val success: Boolean,
    val code: Int,
    val data: PatientRegistrationData
)