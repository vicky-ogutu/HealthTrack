package com.example.healthtrack.RoomDatabase.Daos
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtrack.RoomDatabase.Entities.PatientRegistrationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientRegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientRegistrationEntity)

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientRegistrationEntity>>

    @Query("SELECT * FROM patients WHERE `unique` = :unique")
    suspend fun getPatientById(unique: String): PatientRegistrationEntity?
}