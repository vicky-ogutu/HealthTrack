package com.example.healthtrack.ApiDataClasses

data class VisitRequest(
    val general_health: String,
    val on_diet: String,
    val on_drugs: String,
    val comments: String,
    val visit_date: String,
    val patient_id: String,
    val vital_id: String
)