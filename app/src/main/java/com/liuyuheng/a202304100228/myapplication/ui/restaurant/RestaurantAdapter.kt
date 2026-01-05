package com.liuyuheng.a202304100228.myapplication.ui.restaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liuyuheng.a202304100228.myapplication.R
import com.liuyuheng.a202304100228.myapplication.model.Restaurant

class RestaurantAdapter(private val restaurants: List<Restaurant>) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.restaurant_name)
        val addressTextView: TextView = view.findViewById(R.id.restaurant_address)
        val ratingBar: RatingBar = view.findViewById(R.id.restaurant_rating)
        val descriptionTextView: TextView = view.findViewById(R.id.restaurant_description)
        val cuisineTypeTextView: TextView = view.findViewById(R.id.cuisine_type)
        val priceRangeTextView: TextView = view.findViewById(R.id.price_range)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        try {
            val restaurant = restaurants[position]
            holder.nameTextView.text = restaurant.name
            holder.addressTextView.text = restaurant.address
            holder.ratingBar.rating = restaurant.rating
            holder.descriptionTextView.text = restaurant.description
            holder.cuisineTypeTextView.text = restaurant.cuisineType
            holder.priceRangeTextView.text = restaurant.priceRange
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount() = restaurants.size
}