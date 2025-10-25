package com.fit2081.nutritrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit2081.nutritrack.data.foodintakes.FoodIntake
import com.fit2081.nutritrack.data.foodintakes.FoodIntakeDao
import com.fit2081.nutritrack.data.genai.MotivationalTip
import com.fit2081.nutritrack.data.genai.MotivationalTipDao
import com.fit2081.nutritrack.data.patients.Patient
import com.fit2081.nutritrack.data.patients.PatientDao

@Database(entities = [Patient::class, FoodIntake::class, MotivationalTip::class], version = 4, exportSchema = false)
/**
 * Abstract class representing the nutritrack database.
 * It extends RoomDatabase and provides access to the DAO interfaces for the entities.
 */
// Referenced from W5 Lab
abstract class NutriDatabase : RoomDatabase() {
    abstract fun patientDao() : PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun motivationalTipDao(): MotivationalTipDao

    companion object {
        @Volatile
        private var INSTANCE: NutriDatabase? = null

        fun getDatabase(context: Context): NutriDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, NutriDatabase::class.java, "nutritrack_database")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}