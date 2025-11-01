package com.example.healthtrack.ApiDataClasses

import java.util.Date

data class Patient(
    val patientId: String,
    val registrationDate: Date,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Date,
    val gender: String
)
