package com.liuyuheng.a202304100228.myapplication.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.model.Recipe

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.recipe_name)
        val descriptionTextView: TextView = view.findViewById(R.id.recipe_description)
        val ingredientsTextView: TextView = view.findViewById(R.id.recipe_ingredients)
        val stepsTextView: TextView = view.findViewById(R.id.recipe_steps)
        val cookingTimeTextView: TextView = view.findViewById(R.id.cooking_time)
        val difficultyTextView: TextView = view.findViewById(R.id.difficulty)
        val categoryTextView: TextView = view.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        try {
            val recipe = recipes[position]
            holder.nameTextView.text = recipe.name
            holder.descriptionTextView.text = recipe.description
            holder.ingredientsTextView.text = "食材: ${recipe.ingredients.joinToString(", ")}"
            holder.stepsTextView.text = "步骤: ${recipe.steps.joinToString("\n")}"
            holder.cookingTimeTextView.text = "烹饪时间: ${recipe.cookingTime}"
            holder.difficultyTextView.text = "难度: ${recipe.difficulty}"
            holder.categoryTextView.text = "分类: ${recipe.category}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount() = recipes.size
}
