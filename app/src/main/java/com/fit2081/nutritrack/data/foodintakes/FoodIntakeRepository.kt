package com.fit2081.nutritrack.data.foodintakes

import android.content.Context
import com.fit2081.nutritrack.data.NutriDatabase

// Referenced from W5 Lab
class FoodIntakeRepository(context: Context) {
    private val foodIntakeDao = NutriDatabase.getDatabase(context).foodIntakeDao()

    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insertFoodIntake(foodIntake)
    }

    suspend fun getFoodIntakeByUserId(userId: String): FoodIntake? {
        return foodIntakeDao.getFoodIntakeByUserId(userId)
    }

    suspend fun updateFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.updateFoodIntake(foodIntake)
    }

    suspend fun deleteFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.deleteFoodIntake(foodIntake)
    }
}