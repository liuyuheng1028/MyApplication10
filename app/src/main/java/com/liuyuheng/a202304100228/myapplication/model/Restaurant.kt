package com.liuyuheng.a202304100228.myapplication.model

data class Restaurant(
    val id: Int,
    val name: String,
    val address: String,
    val rating: Float,
    val description: String,
    val cuisineType: String,
    val priceRange: String,
    val imageUrl: String = "",
    val latitude: Double = 39.9042,
    val longitude: Double = 116.4074,
    val phone: String = "",
    val businessHours: String = "",
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
) {
    fun getDistanceFromUser(userLat: Double, userLng: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(latitude - userLat)
        val dLng = Math.toRadians(longitude - userLng)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}