package com.liuyuheng.a202304100228.myapplication.model

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val cookingTime: String,
    val difficulty: String,
    val imageUrl: String = "",
    val category: String
)
