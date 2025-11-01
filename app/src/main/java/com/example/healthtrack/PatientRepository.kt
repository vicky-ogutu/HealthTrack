package com.example.healthtrack

// PatientRepository.kt
import com.example.healthtrack.ApiDataClasses.Patient
import com.example.healthtrack.Entities.PatientEntity
import kotlinx.coroutines.flow.Flow

class PatientRepository(
    private val patientDao: PatientDao,
    private val apiService: PatientApiService
) {
    fun getAllPatients(): Flow<List<PatientEntity>> = patientDao.getAllPatients()

    suspend fun insertPatient(patient: PatientEntity) {
        // Save to local database
        patientDao.insertPatient(patient)

        // Convert to API model and send to server
        val apiPatient = Patient(
            patientId = patient.patientId,
            registrationDate = patient.registrationDate.toString(),
            firstName = patient.firstName,
            lastName = patient.lastName,
            dateOfBirth = patient.dateOfBirth.toString(),
            gender = patient.gender
        )

        try {
            val response = apiService.registerPatient(apiPatient)
            if (!response.isSuccessful) {
                // log API error
                println("API call failed: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            //network error
            println("Network error: ${e.message}")
        }
    }

    suspend fun isPatientIdUnique(patientId: String): Boolean {
        return patientDao.getPatientById(patientId) == null
    }
}