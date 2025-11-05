package com.example.healthtrack.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(dob: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val birthDate = LocalDate.parse(dob, formatter)
    return Period.between(birthDate, LocalDate.now()).years
}