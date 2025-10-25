package com.fit2081.nutritrack.data.patients

import android.content.Context
import com.fit2081.nutritrack.data.NutriDatabase
import kotlinx.coroutines.flow.Flow

// Referenced from W5 Lab
class PatientRepository(context: Context) {
    private val patientDao = NutriDatabase.Companion.getDatabase(context).patientDao()

    suspend fun insert(patient: Patient) {
        patientDao.insert(patient)
    }

    suspend fun insertAll(patient: List<Patient>) {
        patientDao.insertAll(patient)
    }

    suspend fun update(patient: Patient) {
        patientDao.update(patient)
    }

    suspend fun getPatientByUserIdAndPhone(userId: String, phoneNumber: String): Patient? {
        return patientDao.getPatientByUserIdAndPhone(userId, phoneNumber)
    }

    suspend fun getPatientByUserId(userId: String): Patient? {
        return patientDao.getPatientByUserId(userId)
    }

    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()
}