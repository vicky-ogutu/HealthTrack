package com.example.healthtrack.ApiDataClasses

data class VitalRequest(  val visit_date: String,
                          val height: String,
                          val weight: String,
                          val bmi: String,
                          val patient_id: String)
