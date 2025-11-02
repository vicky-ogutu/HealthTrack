package com.example.healthtrack.RoomDatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.healthtrack.RoomDatabase.Entities.PatientRegistrationEntity
import com.example.healthtrack.RoomDatabase.Daos.PatientRegistrationDao

@Database(entities = [PatientRegistrationEntity::class], version = 1)
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