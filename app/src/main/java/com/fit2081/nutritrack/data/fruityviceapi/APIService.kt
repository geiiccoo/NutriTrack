package com.fit2081.nutritrack.data.fruityviceapi

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Referenced from W7 Lab
interface APIService {
    @GET("api/fruit/{fruitName}")
    suspend fun getFruit(@Path("fruitName") fruitName: String): Response<FruitResponse>

    companion object {
        var BASE_URL = "https://www.fruityvice.com/"
        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}