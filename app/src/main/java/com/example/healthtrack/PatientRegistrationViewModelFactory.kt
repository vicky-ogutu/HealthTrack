package com.example.healthtrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthtrack.Repositories.PatientRegistrationRepository
import com.example.healthtrack.ViewModels.PatientRegistrationViewModel // ✅ Add this import

class PatientRegistrationViewModelFactory(
    private val patientRepository: PatientRegistrationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PatientRegistrationViewModel::class.java) -> {
                PatientRegistrationViewModel(patientRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
