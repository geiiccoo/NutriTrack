package com.fit2081.nutritrack.data.foodintakes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Referenced from W5 Lab
class FoodIntakeViewModel(context: Context): ViewModel() {
    private val foodIntakeRepo = FoodIntakeRepository(context)

    fun insertFoodIntake(foodIntake: FoodIntake) = viewModelScope.launch {
        foodIntakeRepo.insertFoodIntake(foodIntake)
    }

    suspend fun getFoodIntakeByUserId(userId: String): FoodIntake? {
        return foodIntakeRepo.getFoodIntakeByUserId(userId)
    }

    fun updateFoodIntake(foodIntake: FoodIntake) = viewModelScope.launch {
        foodIntakeRepo.updateFoodIntake(foodIntake)
    }

    fun deleteFoodIntake(foodIntake: FoodIntake) = viewModelScope.launch {
        foodIntakeRepo.deleteFoodIntake(foodIntake)
    }

    class FoodIntakeViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodIntakeViewModel(context) as T
        }
    }
}