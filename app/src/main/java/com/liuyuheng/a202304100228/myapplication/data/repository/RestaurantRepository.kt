package com.liuyuheng.a202304100228.myapplication.data.repository

import com.liuyuheng.a202304100228.myapplication.data.mock.MockRestaurantData
import com.liuyuheng.a202304100228.myapplication.model.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestaurantRepository {
    
    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(1000)
            MockRestaurantData.getRestaurants()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getRestaurantById(id: Int): Restaurant? = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(500)
            MockRestaurantData.getRestaurants().find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun searchRestaurants(query: String): List<Restaurant> = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(800)
            val allRestaurants = MockRestaurantData.getRestaurants()
            allRestaurants.filter { restaurant ->
                restaurant.name.contains(query, ignoreCase = true) ||
                restaurant.cuisineType.contains(query, ignoreCase = true) ||
                restaurant.description.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
