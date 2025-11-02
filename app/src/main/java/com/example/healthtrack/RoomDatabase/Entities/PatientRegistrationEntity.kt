package com.example.healthtrack.RoomDatabase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "patients")
data class PatientRegistrationEntity(
    @PrimaryKey
    val unique: String,
    val reg_date: Date,
    val firstname: String,
    val lastname: String,
    val dob: Date,
    val gender: String
)
