package com.liuyuheng.a202304100228.myapplication.network

import com.liuyuheng.a202304100228.myapplication.model.Recipe
import com.liuyuheng.a202304100228.myapplication.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // 获取附近餐厅推荐（协程版）
    @GET("restaurants/recommend")
    suspend fun getRecommendRestaurants(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): List<Restaurant>

    // 根据食材推荐菜谱（协程版）
    @GET("recipes/recommend")
    suspend fun getRecommendRecipes(
        @Query("ingredients") ingredients: String // 食材列表，逗号分隔
    ): List<Recipe>
}