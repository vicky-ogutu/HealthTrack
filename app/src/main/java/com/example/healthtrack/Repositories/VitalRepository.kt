package com.example.healthtrack.Repositories

import com.example.healthtrack.RoomDatabase.Daos.VitalDao
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class VitalRepository @Inject constructor(
    private val vitalDao: VitalDao
) {
    suspend fun insertVital(vital: VitalEntity) {
        vitalDao.insertVital(vital)
    }

    fun getVitalsByPatient(patientId: String): Flow<List<VitalEntity>> {
        return vitalDao.getVitalsByPatient(patientId)
    }

    suspend fun getVitalByPatientAndDate(patientId: String, visitDate: Date): VitalEntity? {
        return vitalDao.getVitalByPatientAndDate(patientId, visitDate)
    }
}