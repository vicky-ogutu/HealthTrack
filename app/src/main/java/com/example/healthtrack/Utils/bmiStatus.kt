package com.example.healthtrack.Utils

fun bmiStatus(bmi: Double?): String {
    return when {
        bmi == null -> "Unknown"
        bmi < 18.5 -> "Underweight"
        bmi < 25 -> "Normal"
        else -> "Overweight"
    }
}