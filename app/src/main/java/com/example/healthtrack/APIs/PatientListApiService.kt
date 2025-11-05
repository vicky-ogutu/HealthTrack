package com.example.healthtrack.APIs

import com.example.healthtrack.ApiDataClasses.PatientListResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PatientListApiService {

    @GET("patients/view")
    suspend fun getPatients(
        @Header("Accept") accept: String = "application/json",
        @Header("Authorization") authorization: String,
        @Query("visit_date") visitDate: String? = null
    ): PatientListResponse
}