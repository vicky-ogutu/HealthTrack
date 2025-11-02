package com.example.healthtrack.RoomDatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.healthtrack.RoomDatabase.Entities.PatientRegistrationEntity
import com.example.healthtrack.RoomDatabase.Daos.PatientRegistrationDao
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity

@Database(
    entities = [PatientRegistrationEntity::class, VitalEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientRegistrationDao

    companion object {
        @Volatile
        private var INSTANCE: PatientDatabase? = null

        fun getInstance(context: Context): PatientDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PatientDatabase::class.java,
                    "patient_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}