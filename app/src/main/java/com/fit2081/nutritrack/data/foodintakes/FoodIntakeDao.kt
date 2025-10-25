package com.fit2081.nutritrack.data.foodintakes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
// Referenced from W5 Lab
interface FoodIntakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intakes WHERE patientUserId = :userId LIMIT 1")
    suspend fun getFoodIntakeByUserId(userId: String): FoodIntake?

    @Update
    suspend fun updateFoodIntake(foodIntake: FoodIntake)

    @Delete
    suspend fun deleteFoodIntake(foodIntake: FoodIntake)
}