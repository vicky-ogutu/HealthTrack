package com.example.healthtrack

// PatientApiService.kt
import com.example.healthtrack.ApiDataClasses.Patient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PatientApiService {
    @POST("patients/register")
    suspend fun registerPatient(@Body patient: Patient): Response<Patient>
}