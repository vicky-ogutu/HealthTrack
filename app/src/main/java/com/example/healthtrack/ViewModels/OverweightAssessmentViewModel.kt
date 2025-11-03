package com.example.healthtrack.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.Repositories.VisitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OverweightAssessmentViewModel(private val visitRepository: VisitRepository) : ViewModel() {

    private val _visitDate = MutableStateFlow<Date?>(null)
    val visitDate: StateFlow<Date?> = _visitDate.asStateFlow()

    private val _generalHealth = MutableStateFlow("")
    val generalHealth: StateFlow<String> = _generalHealth.asStateFlow()

    private val _onDrugs = MutableStateFlow("")
    val onDrugs: StateFlow<String> = _onDrugs.asStateFlow()

    private val _onDiet = MutableStateFlow("")
    val onDiet: StateFlow<String> = _onDiet.asStateFlow()

    private val _comments = MutableStateFlow("")
    val comments: StateFlow<String> = _comments.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateVisitDate(date: Date) {
        _visitDate.value = date
    }

    fun updateGeneralHealth(health: String) {
        _generalHealth.value = health
    }

    fun updateOnDrugs(onDrugs: String) {
        _onDrugs.value = onDrugs
    }

    fun updateOnDiet(onDiet: String) {
        _onDiet.value = onDiet
    }

    fun updateComments(comments: String) {
        _comments.value = comments
    }

    fun saveAssessment(patientId: String, vitalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val visitDateValue = _visitDate.value
                val generalHealthValue = _generalHealth.value.trim()
                val onDrugsValue = _onDrugs.value.trim()
                val onDietValue = _onDiet.value.trim()
                val commentsValue = _comments.value.trim()

                if (visitDateValue == null || generalHealthValue.isEmpty() || onDrugsValue.isEmpty() || commentsValue.isEmpty()) {
                    _errorMessage.value = "Please fill all fields"
                    return@launch
                }

                // ✅ Format visit date to string before sending
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val visitDateString = dateFormatter.format(visitDateValue)

                val success = visitRepository.addOverweightAssessment(
                    patientId = patientId,
                    vitalId = vitalId,
                    visitDate = visitDateString, // ✅ fixed (was Date before)
                    generalHealth = generalHealthValue,
                    onDrugs = onDrugsValue,
                    onDiet = onDietValue,
                    comments = commentsValue
                )

                if (success) {
                    _saveSuccess.value = true
                    clearForm()
                } else {
                    _errorMessage.value = "Failed to save assessment"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error saving assessment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearForm() {
        _visitDate.value = null
        _generalHealth.value = ""
        _onDrugs.value = ""
        _onDiet.value = ""
        _comments.value = ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _saveSuccess.value = false
    }

    companion object {
        fun Factory(visitRepository: VisitRepository): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OverweightAssessmentViewModel(visitRepository) as T
                }
            }
        }
    }
}
