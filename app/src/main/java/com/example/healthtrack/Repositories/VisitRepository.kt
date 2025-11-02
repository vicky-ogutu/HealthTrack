package com.example.healthtrack.Repositories

import com.example.healthtrack.APIs.VisitApiService
import com.example.healthtrack.TokenManager
import com.example.healthtrack.ApiDataClasses.VisitRequest
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class VisitRepository @Inject constructor(
    private val visitApiService: VisitApiService,
    private val tokenManager: TokenManager
) {
    suspend fun addGeneralAssessment(
        patientId: String,
        vitalId: String,
        visitDate: Date,
        generalHealth: String,
        onDiet: String,
        comments: String
    ): Boolean {
        return sendToServer(
            VisitRequest(
                general_health = generalHealth,
                on_diet = onDiet,
                on_drugs = null, // Not used in general assessment
                comments = comments,
                visit_date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(visitDate),
                patient_id = patientId,
                vital_id = vitalId
            )
        )
    }

    suspend fun addOverweightAssessment(
        patientId: String,
        vitalId: String,
        visitDate: Date,
        generalHealth: String,
        onDrugs: String,
        comments: String
    ): Boolean {
        return sendToServer(
            VisitRequest(
                general_health = generalHealth,
                on_diet = null, // Not used in overweight assessment
                on_drugs = onDrugs,
                comments = comments,
                visit_date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(visitDate),
                patient_id = patientId,
                vital_id = vitalId
            )
        )
    }

    private suspend fun sendToServer(request: VisitRequest): Boolean {
        try {
            val token = tokenManager.getAccessToken()
            if (token == null) {
                println("No authentication token available. Visit saved locally only.")
                return false
            }

            val response = visitApiService.addVisit(
                authorization = token,
                request = request
            )

            if (response.isSuccessful) {
                println("Visit successfully synced to server: ${response.body()?.data?.message}")
                return true
            } else {
                when (response.code()) {
                    401 -> println("Unauthorized - Invalid token")
                    403 -> println("Forbidden - Insufficient permissions")
                    else -> println("Server error: ${response.code()} - ${response.errorBody()?.string()}")
                }
                return false
            }
        } catch (e: HttpException) {
            println("HTTP error: ${e.message}")
            return false
        } catch (e: Exception) {
            println("Network error: ${e.message}. Visit will be retried later.")
            return false
        }
    }
}