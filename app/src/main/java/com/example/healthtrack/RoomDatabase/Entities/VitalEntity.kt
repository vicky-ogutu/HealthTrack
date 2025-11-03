package com.example.healthtrack.RoomDatabase.Entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.healthtrack.RoomDatabase.Converters
import java.util.*

@Entity(tableName = "vitals")
@TypeConverters(Converters::class)
data class VitalEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val visitDate: Date,
    val height: Double, // in CM
    val weight: Double, // in KG
    val bmi: Double,
    val serverId: Int? = null // Store the server-generated ID
)