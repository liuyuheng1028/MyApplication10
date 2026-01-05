package com.liuyuheng.a202304100228.myapplication.data.repository

import com.liuyuheng.a202304100228.myapplication.data.mock.MockRecipeData
import com.liuyuheng.a202304100228.myapplication.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository {
    
    suspend fun getRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(1000)
            MockRecipeData.getRecipes()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getRecipeById(id: Int): Recipe? = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(500)
            MockRecipeData.getRecipes().find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getRecipesByIngredients(ingredients: List<String>): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(800)
            MockRecipeData.getRecipesByIngredients(ingredients)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun searchRecipes(query: String): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            Thread.sleep(800)
            val allRecipes = MockRecipeData.getRecipes()
            allRecipes.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                recipe.category.contains(query, ignoreCase = true) ||
                recipe.description.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
