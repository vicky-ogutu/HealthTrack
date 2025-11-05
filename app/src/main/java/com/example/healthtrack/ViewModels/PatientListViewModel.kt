package com.example.healthtrack.ViewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.ApiDataClasses.PatientListData
import com.example.healthtrack.Repositories.PatientListRepository
import com.example.healthtrack.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientListViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val repository = PatientListRepository(tokenManager)

    private val _patients = MutableStateFlow<List<PatientListData>>(emptyList())
    val patients: StateFlow<List<PatientListData>> = _patients

    fun loadPatients(visitDate: String? = null) {
        viewModelScope.launch {
            repository.getPatients(visitDate).collect { response ->
                if (response.success) {
                    _patients.value = response.data
                }
            }
        }
    }

    class Factory(
        private val tokenManager: TokenManager
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PatientListViewModel(tokenManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


