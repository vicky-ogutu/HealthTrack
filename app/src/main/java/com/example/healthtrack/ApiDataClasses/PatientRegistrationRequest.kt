package com.example.healthtrack.ApiDataClasses

data class PatientRegistrationRequest(
    val firstname: String,
    val lastname: String,
    val unique: String,
    val dob: String,
    val gender: String,
    val reg_date: String
)
