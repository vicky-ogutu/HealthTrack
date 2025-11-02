package com.example.healthtrack.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface VitalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vital: VitalEntity)

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY visitDate DESC")
    fun getVitalsByPatient(patientId: String): Flow<List<VitalEntity>>

    @Query("SELECT * FROM vitals WHERE patientId = :patientId AND visitDate = :visitDate")
    suspend fun getVitalByPatientAndDate(patientId: String, visitDate: Date): VitalEntity?
}