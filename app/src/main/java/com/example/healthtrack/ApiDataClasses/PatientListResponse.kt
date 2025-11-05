package com.example.healthtrack.ApiDataClasses

data class PatientListResponse(
    val message: String,
    val success: Boolean,
    val code: Int,
    val data: List<PatientListData>
)