package com.example.healthtrack.ApiDataClasses

data class VitalResponse( val message: String,
                          val success: Boolean,
                          val code: Int,
                          val data: VitalData)
