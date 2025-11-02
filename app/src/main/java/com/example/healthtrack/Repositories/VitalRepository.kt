package com.example.healthtrack.Repositories

import com.example.healthtrack.APIs.VitalApiService
import com.example.healthtrack.RoomDatabase.Daos.VitalDao
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity

import com.example.healthtrack.TokenManager
import com.example.healthtrack.ApiDataClasses.VitalRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class VitalRepository @Inject constructor(
    private val vitalDao: VitalDao,
    private val vitalApiService: VitalApiService,
    private val tokenManager: TokenManager
) {
    suspend fun insertVital(vital: VitalEntity) {
        // Save to local database first
        vitalDao.insertVital(vital)

        // Convert to API request format and send to server
        sendToServer(vital)
    }

    fun getVitalsByPatient(patientId: String): Flow<List<VitalEntity>> {
        return vitalDao.getVitalsByPatient(patientId)
    }

    suspend fun getVitalByPatientAndDate(patientId: String, visitDate: Date): VitalEntity? {
        return vitalDao.getVitalByPatientAndDate(patientId, visitDate)
    }

    private suspend fun sendToServer(vital: VitalEntity) {
        try {
            val token = tokenManager.getAccessToken()
            if (token == null) {
                println("No authentication token available. Vital saved locally only.")
                return
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val apiRequest = VitalRequest(
                visit_date = dateFormat.format(vital.visitDate),
                height = vital.height.toString(),
                weight = vital.weight.toString(),
                bmi = String.format("%.2f", vital.bmi),
                patient_id = vital.patientId
            )

            val response = vitalApiService.addVital(
                authorization = token,
                request = apiRequest
            )

            if (response.isSuccessful) {
                println("Vital successfully synced to server: ${response.body()?.data?.message}")
            } else {
                // Handle different HTTP error codes
                when (response.code()) {
                    401 -> println("Unauthorized - Invalid token")
                    403 -> println("Forbidden - Insufficient permissions")
                    else -> println("Server error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }
        } catch (e: HttpException) {
            println("HTTP error: ${e.message}")
        } catch (e: Exception) {
            println("Network error: ${e.message}. Vital saved locally, will sync when possible.")
        }
    }
}