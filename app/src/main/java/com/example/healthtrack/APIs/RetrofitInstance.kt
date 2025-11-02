package com.example.healthtrack.APIs

// RetrofitInstance.kt
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://patientvisitapis.intellisoftkenya.com/api/"

    val apiService: PatientRegistrationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PatientRegistrationApiService::class.java)
    }
}