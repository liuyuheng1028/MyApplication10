package com.liuyuheng.a202304100228.myapplication.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.data.repository.RecipeRepository
import com.liuyuheng.a202304100228.myapplication.model.Recipe
import kotlinx.coroutines.launch

class RecipeFragment : Fragment() {
    companion object {
        private const val TAG = "RecipeFragment"
    }
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var repository: RecipeRepository
    private lateinit var ingredientInput: EditText
    private lateinit var searchButton: Button
    private lateinit var showAllButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    
    private var allRecipes: List<Recipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: 开始创建RecipeFragment")
        val view = inflater.inflate(R.layout.fragment_recipe, container, false)
        
        repository = RecipeRepository()
        
        recyclerView = view.findViewById(R.id.recipe_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        ingredientInput = view.findViewById(R.id.ingredient_input)
        searchButton = view.findViewById(R.id.search_button)
        showAllButton = view.findViewById(R.id.show_all_button)
        progressBar = view.findViewById(R.id.progress_bar)
        errorText = view.findViewById(R.id.error_text)
        
        searchButton.setOnClickListener {
            val ingredients = ingredientInput.text.toString().split("，", ",").map { it.trim() }.filter { it.isNotEmpty() }
            if (ingredients.isNotEmpty()) {
                searchRecipesByIngredients(ingredients)
            }
        }
        
        showAllButton.setOnClickListener {
            loadAllRecipes()
        }
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 开始加载菜谱数据")
        loadAllRecipes()
    }
    
    private fun loadAllRecipes() {
        try {
            Log.d(TAG, "loadAllRecipes: 开始加载")
            progressBar.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            recyclerView.visibility = View.GONE
            
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    allRecipes = repository.getRecipes()
                    progressBar.visibility = View.GONE
                    
                    if (allRecipes.isNotEmpty()) {
                        adapter = RecipeAdapter(allRecipes)
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                        Log.d(TAG, "loadAllRecipes: 加载成功，共 ${allRecipes.size} 个菜谱")
                    } else {
                        errorText.text = "暂无菜谱数据"
                        errorText.visibility = View.VISIBLE
                        Log.w(TAG, "loadAllRecipes: 没有菜谱数据")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "loadAllRecipes: 加载失败", e)
                    progressBar.visibility = View.GONE
                    errorText.text = "加载失败：${e.message}"
                    errorText.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "loadAllRecipes: 外部异常", e)
        }
    }
    
    private fun searchRecipesByIngredients(ingredients: List<String>) {
        try {
            Log.d(TAG, "searchRecipesByIngredients: 搜索食材 - ${ingredients.joinToString(", ")}")
            progressBar.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            recyclerView.visibility = View.GONE
            
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val recipes = repository.getRecipesByIngredients(ingredients)
                    progressBar.visibility = View.GONE
                    
                    if (recipes.isNotEmpty()) {
                        adapter = RecipeAdapter(recipes)
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                        Log.d(TAG, "searchRecipesByIngredients: 搜索成功，找到 ${recipes.size} 个菜谱")
                    } else {
                        errorText.text = "没有找到匹配的菜谱"
                        errorText.visibility = View.VISIBLE
                        Log.w(TAG, "searchRecipesByIngredients: 没有找到匹配的菜谱")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "searchRecipesByIngredients: 搜索失败", e)
                    progressBar.visibility = View.GONE
                    errorText.text = "搜索失败：${e.message}"
                    errorText.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "searchRecipesByIngredients: 外部异常", e)
        }
    }
}
