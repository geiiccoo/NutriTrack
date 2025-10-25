package com.fit2081.nutritrack.data.patients

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Referenced from W5 Lab
class PatientViewModel(context: Context) : ViewModel() {
    private val patientRepo = PatientRepository(context)

    val allPatients: Flow<List<Patient>> = patientRepo.getAllPatients()

    fun insert(patient: Patient) = viewModelScope.launch {
        patientRepo.insert(patient)
    }

    fun insertAll(patient: List<Patient>) = viewModelScope.launch {
        patientRepo.insertAll(patient)
    }

    fun update(patient: Patient) = viewModelScope.launch {
        patientRepo.update(patient)
    }

    suspend fun getPatientByUserIdAndPhone(userId: String, phoneNumber: String): Patient? {
        return patientRepo.getPatientByUserIdAndPhone(userId, phoneNumber)
    }

    suspend fun getPatientByUserId(userId: String): Patient? {
        return patientRepo.getPatientByUserId(userId)
    }

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    fun loadPatient(userId: String) {
        viewModelScope.launch {
            val loadedPatient = patientRepo.getPatientByUserId(userId)
            Log.d("PatientViewModel", "Loaded patient: $loadedPatient")
            _patient.value = loadedPatient
        }
    }

    class PatientViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientViewModel(context) as T
        }
    }
}