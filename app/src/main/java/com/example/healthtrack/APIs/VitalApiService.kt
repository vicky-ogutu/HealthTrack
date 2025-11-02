package com.example.healthtrack.APIs

import com.example.healthtrack.ApiDataClasses.VitalRequest
import com.example.healthtrack.ApiDataClasses.VitalResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VitalApiService {
    @POST("vital/add")
    suspend fun addVital(
        @Header("Accept") accept: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body request: VitalRequest
    ): Response<VitalResponse>
}