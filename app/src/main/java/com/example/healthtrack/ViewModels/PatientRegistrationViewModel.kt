package com.example.healthtrack.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.RoomDatabase.Entities.PatientRegistrationEntity
import com.example.healthtrack.Repositories.PatientRegistrationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class PatientRegistrationViewModel(private val repository: PatientRegistrationRepository) : ViewModel() {

    // Form state
    private val _patientId = MutableStateFlow("")
    val patientId: StateFlow<String> = _patientId.asStateFlow()

    private val _registrationDate = MutableStateFlow<Date?>(null)
    val registrationDate: StateFlow<Date?> = _registrationDate.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _dateOfBirth = MutableStateFlow<Date?>(null)
    val dateOfBirth: StateFlow<Date?> = _dateOfBirth.asStateFlow()

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _isPatientIdUnique = MutableStateFlow(true)
    val isPatientIdUnique: StateFlow<Boolean> = _isPatientIdUnique.asStateFlow()

    // Form validation
    val isFormValid: StateFlow<Boolean> = combine(
        _patientId,
        _registrationDate,
        _firstName,
        _lastName,
        _dateOfBirth,
        _gender,
        _isPatientIdUnique
    ) { values: Array<Any?> ->
        val patientId = values[0] as String
        val regDate = values[1] as Date?
        val firstName = values[2] as String
        val lastName = values[3] as String
        val dob = values[4] as Date?
        val gender = values[5] as String
        val isUnique = values[6] as Boolean

        patientId.isNotBlank() && regDate != null && firstName.isNotBlank() &&
                lastName.isNotBlank() && dob != null && gender.isNotBlank() && isUnique
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun updatePatientId(patientId: String) {
        _patientId.value = patientId
        if (patientId.isNotBlank()) {
            checkPatientIdUnique(patientId)
        } else {
            _isPatientIdUnique.value = true
        }
    }

    fun updateRegistrationDate(date: Date) {
        _registrationDate.value = date
    }

    fun updateFirstName(firstName: String) {
        _firstName.value = firstName
    }

    fun updateLastName(lastName: String) {
        _lastName.value = lastName
    }

    fun updateDateOfBirth(date: Date) {
        _dateOfBirth.value = date
    }

    fun updateGender(gender: String) {
        _gender.value = gender
    }

    fun generatePatientId() {
        val newId = "PAT-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
        _patientId.value = newId
        checkPatientIdUnique(newId)
    }

    private fun checkPatientIdUnique(patientId: String) {
        viewModelScope.launch {
            _isPatientIdUnique.value = repository.isPatientIdUnique(patientId)
        }
    }

    fun savePatient() {
        viewModelScope.launch {
            try {
                val registrationDateValue = registrationDate.value
                val dateOfBirthValue = dateOfBirth.value

                if (registrationDateValue != null && dateOfBirthValue != null) {
                    val patient = PatientRegistrationEntity(
                        unique = patientId.value,
                        reg_date = registrationDateValue,
                        firstname = firstName.value,
                        lastname = lastName.value,
                        dob = dateOfBirthValue,
                        gender = gender.value
                    )

                    repository.insertPatient(patient)
                    // Clear form after successful save
                    clearForm()
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun clearForm() {
        _patientId.value = ""
        _registrationDate.value = null
        _firstName.value = ""
        _lastName.value = ""
        _dateOfBirth.value = null
        _gender.value = ""
        _isPatientIdUnique.value = true
    }
}