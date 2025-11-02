package com.example.healthtrack.ApiDataClasses

data class VisitResponse(
    val message: String,
    val success: Boolean,
    val code: Int,
    val data: VisitData
)