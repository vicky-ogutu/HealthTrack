package com.example.healthtrack.APIs

// PatientApiService.kt
import com.example.healthtrack.ApiDataClasses.PatientRegistrationRequest
import com.example.healthtrack.ApiDataClasses.PatientRegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PatientRegistrationApiService {
    @POST("patients/register")
    suspend fun registerPatient(
        @Header("Accept") accept: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body request: PatientRegistrationRequest
    ): Response<PatientRegistrationResponse>
}