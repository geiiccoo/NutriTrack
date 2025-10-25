package com.fit2081.nutritrack.data.patients

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
// Referenced from W5 Lab
interface PatientDao {
    @Insert
    suspend fun insert(patient: Patient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    @Update
    suspend fun update(patient: Patient)

    @Query("SELECT * FROM patients WHERE userId = :userId AND phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getPatientByUserIdAndPhone(userId: String, phoneNumber: String): Patient?

    @Query("SELECT * FROM patients WHERE userId = :userId LIMIT 1")
    suspend fun getPatientByUserId(userId: String): Patient?

    @Query("SELECT * FROM patients ORDER BY userId ASC")
    fun getAllPatients(): Flow<List<Patient>>
}