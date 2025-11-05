package com.example.healthtrack.Repositories

import com.example.healthtrack.ApiDataClasses.PatientListResponse
import com.example.healthtrack.RetrofitInstance
import com.example.healthtrack.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PatientListRepository(private val tokenManager: TokenManager) {

    fun getPatients(visitDate: String? = null): Flow<PatientListResponse> = flow {
        val token = tokenManager.getAccessToken()
        val bearerToken = "Bearer $token"

        val response = RetrofitInstance.patientListApiService.getPatients(
            authorization = bearerToken,
            visitDate = visitDate
        )
        emit(response)
    }
}
