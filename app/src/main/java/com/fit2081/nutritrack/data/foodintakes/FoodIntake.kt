package com.fit2081.nutritrack.data.foodintakes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fit2081.nutritrack.data.patients.Patient

@Entity(tableName = "food_intakes",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["patientUserId"]
    )],
    indices = [Index(value = ["patientUserId"])])
// Referenced from W5 Lab
data class FoodIntake(
    @PrimaryKey
    @ColumnInfo(name = "patientUserId")
    val patientUserId: String,
    val selectedCategories: String,
    val selectedPersona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeUpTime: String
)
