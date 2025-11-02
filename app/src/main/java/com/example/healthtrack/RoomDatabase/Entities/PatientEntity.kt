package com.example.healthtrack.RoomDatabase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    val patientId: String,
    val registrationDate: Date,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Date,
    val gender: String
)
