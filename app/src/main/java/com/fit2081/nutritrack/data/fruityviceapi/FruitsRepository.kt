package com.fit2081.nutritrack.data.fruityviceapi

// Referenced from W7 Lab
class FruitsRepository() {
    private val apiService = APIService.create()

    suspend fun getFruit(fruitName: String): FruitResponse? {
        val response = apiService.getFruit(fruitName)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}