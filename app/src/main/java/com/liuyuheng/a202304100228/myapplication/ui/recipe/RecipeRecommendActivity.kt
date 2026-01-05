package com.liuyuheng.a202304100228.myapplication.ui.recipe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeRecommendActivity : AppCompatActivity() {
    private val TAG = "RecipeRecommend"
    private var isLoading = false
    private val recipeCache = mutableMapOf<String, List<com.liuyuheng.a202304100228.myapplication.model.Recipe>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recommend)

        // 示例：传入食材列表（番茄,鸡蛋,米饭）
        getRecommendRecipes("番茄,鸡蛋,米饭")
    }

    // 根据食材获取推荐菜谱（协程版）
    private fun getRecommendRecipes(ingredients: String) {
        if (isLoading) {
            Log.d(TAG, "正在加载中，请稍候")
            return
        }

        if (ingredients.isBlank()) {
            Toast.makeText(this, "请输入食材", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            isLoading = true
            try {
                val recipes = fetchRecipesWithRetry(ingredients)

                withContext(Dispatchers.Main) {
                    if (recipes.isEmpty()) {
                        Log.d(TAG, "未找到相关菜谱")
                        Toast.makeText(this@RecipeRecommendActivity,
                            "未找到相关菜谱，请尝试其他食材", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "获取到${recipes.size}个推荐菜谱")
                        recipes.forEach { recipe ->
                            Log.d(TAG, "菜谱：${recipe.name}")
                        }
                        Toast.makeText(this@RecipeRecommendActivity,
                            "成功获取${recipes.size}个菜谱", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "请求失败：${e.message}")
                    Toast.makeText(this@RecipeRecommendActivity,
                        "获取菜谱失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    // 带重试机制的菜谱获取
    private suspend fun fetchRecipesWithRetry(ingredients: String, maxRetries: Int = 3): List<com.liuyuheng.a202304100228.myapplication.model.Recipe> {
        var lastException: Exception? = null
        
        repeat(maxRetries) { retryCount ->
            try {
                // 检查缓存
                val cachedRecipes = recipeCache[ingredients]
                if (cachedRecipes != null) {
                    Log.d(TAG, "从缓存获取菜谱：${cachedRecipes.size}个")
                    return cachedRecipes
                }

                // 网络请求
                val recipes = RetrofitClient.apiService.getRecommendRecipes(ingredients)
                
                // 缓存结果
                recipeCache[ingredients] = recipes
                return recipes
            } catch (e: Exception) {
                lastException = e
                Log.e(TAG, "请求失败，第${retryCount + 1}次重试：${e.message}")
                if (retryCount < maxRetries - 1) {
                    delay(1000L * (retryCount + 1))
                }
            }
        }
        
        throw lastException ?: Exception("未知错误")
    }
}