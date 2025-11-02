package com.example.healthtrack.APIs

import com.example.healthtrack.ApiDataClasses.VisitRequest
import com.example.healthtrack.ApiDataClasses.VisitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VisitApiService {
    @POST("visits/add")
    suspend fun addVisit(
        @Header("Accept") accept: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body request: VisitRequest
    ): Response<VisitResponse>
}