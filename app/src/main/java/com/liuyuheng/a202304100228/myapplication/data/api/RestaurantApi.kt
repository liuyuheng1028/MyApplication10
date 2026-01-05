package com.liuyuheng.a202304100228.myapplication.data.api

import com.liuyuheng.a202304100228.myapplication.model.Restaurant
import retrofit2.http.GET

interface RestaurantApi {
    @GET("restaurants")
    suspend fun getRestaurants(): List<Restaurant>
}
