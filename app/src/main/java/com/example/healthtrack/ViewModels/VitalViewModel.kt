package com.example.healthtrack.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.RoomDatabase.Entities.VitalEntity
import com.example.healthtrack.Repositories.VitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class VitalViewModel(private val vitalRepository: VitalRepository) : ViewModel() {

    // Form state
    private val _visitDate = MutableStateFlow<Date?>(null)
    val visitDate: StateFlow<Date?> = _visitDate.asStateFlow()

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height.asStateFlow()

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight.asStateFlow()

    private val _bmi = MutableStateFlow<Double?>(null)
    val bmi: StateFlow<Double?> = _bmi.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Calculate BMI when height or weight changes
    fun updateHeight(height: String) {
        _height.value = height
        calculateBMI()
    }

    fun updateWeight(weight: String) {
        _weight.value = weight
        calculateBMI()
    }

    fun updateVisitDate(date: Date) {
        _visitDate.value = date
    }

    private fun calculateBMI() {
        val heightValue = _height.value.toDoubleOrNull()
        val weightValue = _weight.value.toDoubleOrNull()

        if (heightValue != null && weightValue != null && heightValue > 0) {
            val heightInMeters = heightValue / 100
            val calculatedBMI = weightValue / (heightInMeters * heightInMeters)
            _bmi.value = String.format("%.2f", calculatedBMI).toDouble()
        } else {
            _bmi.value = null
        }
    }

    fun saveVital(patientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val visitDateValue = _visitDate.value
                val heightValue = _height.value.toDoubleOrNull()
                val weightValue = _weight.value.toDoubleOrNull()
                val bmiValue = _bmi.value

                if (visitDateValue == null || heightValue == null || weightValue == null || bmiValue == null) {
                    _errorMessage.value = "Please fill all fields"
                    return@launch
                }

                // Check if there's already a vital for this patient on the same date
                val existingVital = vitalRepository.getVitalByPatientAndDate(patientId, visitDateValue)
                if (existingVital != null) {
                    _errorMessage.value = "Vitals already recorded for this date"
                    return@launch
                }

                val vital = VitalEntity(
                    patientId = patientId,
                    visitDate = visitDateValue,
                    height = heightValue,
                    weight = weightValue,
                    bmi = bmiValue
                )

                vitalRepository.insertVital(vital)
                _saveSuccess.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error saving vitals: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearForm() {
        _visitDate.value = null
        _height.value = ""
        _weight.value = ""
        _bmi.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _saveSuccess.value = false
    }

    companion object {
        fun Factory(vitalRepository: VitalRepository): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VitalViewModel(vitalRepository) as T
                }
            }
        }
    }
}