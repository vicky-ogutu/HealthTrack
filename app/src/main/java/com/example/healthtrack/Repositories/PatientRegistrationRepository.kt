package com.example.healthtrack.Repositories

import com.example.healthtrack.APIs.PatientRegistrationApiService
import com.example.healthtrack.ApiDataClasses.PatientRegistrationData
import com.example.healthtrack.TokenManager
import com.example.healthtrack.ApiDataClasses.PatientRegistrationRequest
import com.example.healthtrack.RoomDatabase.Daos.PatientRegistrationDao
import com.example.healthtrack.RoomDatabase.Entities.PatientRegistrationEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class PatientRegistrationRepository(
    private val patientDao: PatientRegistrationDao,
    private val patientApiService: PatientRegistrationApiService,
    private val tokenManager: TokenManager
) {
    fun getAllPatients(): Flow<List<PatientRegistrationEntity>> = patientDao.getAllPatients()

    suspend fun insertPatient(patient: PatientRegistrationEntity) {
        // Save to local database first
        patientDao.insertPatient(patient)

        // Convert to API request format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val apiRequest = PatientRegistrationRequest(
            firstname = patient.firstname,
            lastname = patient.lastname,
            unique = patient.unique,
            dob = dateFormat.format(patient.dob),
            gender = patient.gender,
            reg_date = dateFormat.format(patient.reg_date)
        )

        // Send to server with Bearer token
        sendToServer(apiRequest)
    }

    private suspend fun sendToServer(request: PatientRegistrationRequest) {
        try {
            val token = tokenManager.getAccessToken()
            if (token == null) {
                println("No authentication token available. Patient saved locally only.")
                return
            }

            val response = patientApiService.registerPatient(authorization = token, request = request)

            if (response.isSuccessful) {
                println("Patient successfully synced to server: ${response.body()?.data?.message}")
            } else {
                when (response.code()) {
                    401 -> println("Unauthorized - Invalid token")
                    403 -> println("Forbidden - Insufficient permissions")
                    else -> println("Server error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }
        } catch (e: HttpException) {
            println("HTTP error: ${e.message}")
        } catch (e: Exception) {
            println("Error: ${e.message}. Patient saved locally, will sync when possible.")
        }
    }

    suspend fun isPatientIdUnique(patientId: String): Boolean {
        return patientDao.getPatientById(patientId) == null
    }
}