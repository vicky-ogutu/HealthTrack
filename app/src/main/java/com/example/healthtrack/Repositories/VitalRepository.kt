package com.example.healthtrack.Repositories

import com.example.healthtrack.RoomDatabase.Daos.VitalDao
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity
import com.example.healthtrack.APIs.VitalApiService
import com.example.healthtrack.TokenManager
import com.example.healthtrack.ApiDataClasses.VitalRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class VitalRepository @Inject constructor(
    private val vitalDao: VitalDao,
    private val vitalApiService: VitalApiService,
    private val tokenManager: TokenManager
) {
    // Update to return the saved vital
    suspend fun insertVital(vital: VitalEntity): VitalEntity {
        // Save to local database first
        vitalDao.insertVital(vital)

        // Send to server and get the server ID
        val serverId = sendToServer(vital)

        // Update the local record with server ID if available
        if (serverId != null) {
            val updatedVital = vital.copy(serverId = serverId)
            vitalDao.insertVital(updatedVital)
            return updatedVital
        }

        return vital
    }

    fun getVitalsByPatient(patientId: String): Flow<List<VitalEntity>> {
        return vitalDao.getVitalsByPatient(patientId)
    }

    suspend fun getVitalByPatientAndDate(patientId: String, visitDate: Date): VitalEntity? {
        return vitalDao.getVitalByPatientAndDate(patientId, visitDate)
    }

    // Get the latest vital for a patient
    suspend fun getLatestVitalByPatient(patientId: String): VitalEntity? {
        val vitals = vitalDao.getVitalsByPatient(patientId).firstOrNull()
        if (vitals != null) {
            return vitals.maxByOrNull { it.visitDate }
        }
        return TODO("Provide the return value")
    }

    private suspend fun sendToServer(vital: VitalEntity): Int? {
        try {
            val token = tokenManager.getAccessToken()
            if (token == null) {
                println("No authentication token available. Vital saved locally only.")
                return null
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
                val serverId = response.body()?.data?.id
                println("Vital successfully synced to server with ID: $serverId")
                return serverId
            } else {
                when (response.code()) {
                    401 -> println("Unauthorized - Invalid token")
                    403 -> println("Forbidden - Insufficient permissions")
                    else -> println("Server error: ${response.code()} - ${response.errorBody()?.string()}")
                }
                return null
            }
        } catch (e: HttpException) {
            println("HTTP error: ${e.message}")
            return null
        } catch (e: Exception) {
            println("Network error: ${e.message}. Vital saved locally, will sync when possible.")
            return null
        }
    }
}