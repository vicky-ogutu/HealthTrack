package com.example.healthtrack.ApiDataClasses

data class PatientListData(
    val id: Int,
    val unique: String,
    val firstname: String,
    val lastname: String,
    val dob: String,
    val gender: String,
    val reg_date: String,
    val created_at: String,
    val updated_at: String,
    val last_bmi: Double? = null// optional, not in api
)