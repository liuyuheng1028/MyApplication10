package com.liuyuheng.a202304100228.myapplication.ui.recipe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeRecommendActivity : AppCompatActivity() {
    private val TAG = "RecipeRecommend"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recommend)

        // 示例：传入食材列表（番茄,鸡蛋,米饭）
        getRecommendRecipes("番茄,鸡蛋,米饭")
    }

    // 根据食材获取推荐菜谱（协程版）
    private fun getRecommendRecipes(ingredients: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 网络请求（IO线程）
                val recipes = RetrofitClient.apiService.getRecommendRecipes(ingredients)

                // 切换到主线程更新UI
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "获取到${recipes.size}个推荐菜谱")
                    recipes.forEach { recipe ->
                        Log.d(TAG, "菜谱：${recipe.name}")
                    }
                    Toast.makeText(this@RecipeRecommendActivity,
                        "成功获取${recipes.size}个菜谱", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // 异常处理
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "请求失败：${e.message}")
                    Toast.makeText(this@RecipeRecommendActivity,
                        "获取菜谱失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}