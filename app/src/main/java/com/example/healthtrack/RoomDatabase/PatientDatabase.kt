package com.example.healthtrack.RoomDatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.healthtrack.RoomDatabase.Entities.PatientRegistrationEntity
import com.example.healthtrack.RoomDatabase.Daos.PatientRegistrationDao
import com.example.healthtrack.RoomDatabase.Daos.VitalDao
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity

@Database(
    entities = [PatientRegistrationEntity::class, VitalEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientRegistrationDao
    abstract fun vitalDao(): VitalDao

    companion object {
        @Volatile
        private var INSTANCE: PatientDatabase? = null

        fun getInstance(context: Context): PatientDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PatientDatabase::class.java,
                    "patient_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}