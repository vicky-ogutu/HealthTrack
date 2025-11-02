// RetrofitInstance.kt
package com.example.healthtrack

import com.example.healthtrack.APIs.AuthApiService
import com.example.healthtrack.APIs.PatientRegistrationApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // Update this with your actual base URL
    private const val BASE_URL = "https://patientvisitapis.intellisoftkenya.com/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val patientApiService: PatientRegistrationApiService by lazy {
        retrofit.create(PatientRegistrationApiService::class.java)
    }
}
       //private const val BASE_URL = "https://patientvisitapis.intellisoftkenya.com/api/" // Replace with your actual base URL

