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

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun addGeneralAssessment(
        patientId: String,
        vitalId: String,
        visitDate: Date,
        generalHealth: String,
        onDiet: String,
        onDrugs: String,
        comments: String
    ): Boolean {
        val formattedDate = dateFormatter.format(visitDate)
        val request = VisitRequest(
            general_health = generalHealth,
            on_diet = onDiet,
            on_drugs = onDrugs,
            comments = comments,
            visit_date = formattedDate,
            patient_id = patientId,
            vital_id = vitalId
        )
        return sendToServer(request)
    }

    suspend fun addOverweightAssessment(
        patientId: String,
        vitalId: String,
        visitDate: String, // ✅ Now expects String, not Date
        generalHealth: String,
        onDrugs: String,
        onDiet: String,
        comments: String
    ): Boolean
    {
        val formattedDate = dateFormatter.format(visitDate)
        val request = VisitRequest(
            general_health = generalHealth,
            on_diet = onDiet,
            on_drugs = onDrugs,
            comments = comments,
            visit_date = formattedDate,
            patient_id = patientId,
            vital_id = vitalId
        )
        return sendToServer(request)
    }

    private suspend fun sendToServer(request: VisitRequest): Boolean {
        return try {
            val token = tokenManager.getAccessToken()
            if (token == null) {
                println(" No authentication token available. Visit saved locally only.")
                return false
            }

            val response = visitApiService.addVisit(
                authorization = token,
                request = request
            )

            if (response.isSuccessful) {
                println(" Visit successfully synced to server: ${response.body()?.data?.message}")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                println(" Server error ${response.code()}: $errorBody")
                false
            }
        } catch (e: HttpException) {
            println(" HTTP Exception: ${e.message}")
            false
        } catch (e: Exception) {
            println(" Network/Unexpected Error: ${e.message}")
            false
        }
    }
}
