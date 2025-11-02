package com.example.healthtrack.ApiDataClasses

data class VisitRequest(
    val general_health: String,
    val on_diet: String? = null,
    val on_drugs: String? = null,
    val comments: String,
    val visit_date: String,
    val patient_id: String,
    val vital_id: String
)